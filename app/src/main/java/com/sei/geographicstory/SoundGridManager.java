package com.sei.geographicstory;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.sei.geographicstory.location.GridSquare;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kevin on 12/16/15.
 */
public class SoundGridManager {
    private static SoundGridManager singleton = new SoundGridManager();

    //File upload and download constants
    private static String FILE_UPLOAD_URL = "http://kevinmkarol.com:1217";
    private static String FILE_DOWNLOAD_URL_BASE = "http://kevinmkarol.com:1217";
    private static String lineEnd = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "AaB03x87yxdkjnxvi7";

    //Internal Grid Information
    private ArrayList<ArrayList<GridSquare>> currentSoundGrid;
    private static final int gridSize = 10;
    private int currentXIndex;
    private int currentYIndex;

    //for determining which sound action to perform in a given grid
    private static int randomRange = 100;
    private static int oddsOfNewWord = 20;


    /*  Private Constructor preventing other classes from instantiating
     *
     */
    private SoundGridManager(){
        currentXIndex = -1;
        currentYIndex = -1;

        //Construct Sound Grid
        currentSoundGrid = new ArrayList<ArrayList<GridSquare>>();
        for(int i = 0; i < gridSize; i++){
            ArrayList<GridSquare> row = new ArrayList<GridSquare>();
            for(int j = 0; j < gridSize; j++){
                row.add(new GridSquare());
            }
            currentSoundGrid.add(row);
        }
    }

    public static SoundGridManager getInstance(){return singleton;}

    public void performActionForLocation(GridSquare square){
        findSquareInGrid(square);
        //Next update will cause the action to be performed
        if(currentXIndex != -1 && currentYIndex != -1) {
            SoundLocationWrapper soundAtSquare = currentSoundGrid.get(currentYIndex)
                    .get(currentXIndex).getSoundWrapper();

            Random rand = new Random();

            int randomNum = rand.nextInt(randomRange);

            //Determine what action to perform for square
            if (soundAtSquare.getmSoundId() == null || randomNum < oddsOfNewWord) {
                ActionCoordinator.getInstance().recordSoundForSquare(soundAtSquare);
            } else {
                ActionCoordinator.getInstance().playSoundForSquare(soundAtSquare);
            }
        }
    }

    private void updateSquare(int xCord, int yCord, SoundLocationWrapper wrapper, Location location){
        currentSoundGrid.get(yCord).get(xCord).setSoundWrapper(wrapper);
        currentSoundGrid.get(yCord).get(xCord).setLocation(location);
    }


    private void findSquareInGrid(GridSquare square){
        currentYIndex = -1;
        currentXIndex = -1;
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                GridSquare squareAtLocation = currentSoundGrid.get(i).get(j);
                if(square.isSameSquare(squareAtLocation)){
                    currentXIndex = j;
                    currentYIndex = i;
                    break;
                }
            }
            //break out of second loop when square is found
            if(currentYIndex != -1 && currentXIndex != -1){
                break;
            }
        }

        //If the square doesn't exist in the current grid, download next grid of sounds
        if(currentXIndex == -1 && currentYIndex == -1){
            uploadDirtySoundFiles();
            downloadGridAtLocation(square.getLocation());
        }
    }

    private void uploadDirtySoundFiles(){
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                SoundLocationWrapper wrapper = currentSoundGrid.get(i).get(j).getSoundWrapper();
                if(wrapper != null && wrapper.isDirty()){
                    try {
                        uploadFile(wrapper.getFilePath());
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /* code adapted from http://stackoverflow.com/questions/4966910/androidhow-to-upload-mp3-file-to-http-server*/
    private void uploadFile(String path) throws IOException{
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        FileInputStream fileInputStream = null;

        byte[] buffer;
        int maxBufferSize = 20 * 1024;
        try {
            //------------------ CLIENT REQUEST
            File f = new File(path);
            fileInputStream = new FileInputStream(f);

            // open a URL connection to the Servlet
            // Open a HTTP connection to the URL
            URL url = new URL(FILE_UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data"//; name=\"" + fileParameterName
                    + "\"; filename=\"" + f.toString() + "\"" + lineEnd);
            dos.writeBytes("Content-Type: text/xml" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            buffer = new byte[Math.min((int) f.length(), maxBufferSize)];
            int length;
            // read file and write it into form...
            while ((length = fileInputStream.read(buffer)) != -1) {
                dos.write(buffer, 0, length);
            }

            // send multipart form data necessary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(ProtocolException e){
            e.printStackTrace();
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        finally {
            if (fileInputStream != null) fileInputStream.close();
            if (dos != null) dos.close();
        }

        //------------------ read the SERVER RESPONSE
        try {
            dis = new DataInputStream(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = dis.readLine()) != null) {
                response.append(line).append('\n');
            }


            Log.d("File Upload Response:", response.toString());
        } finally {
            if (dis != null) dis.close();
        }
    }

    private void downloadGridAtLocation(Location l){
        GridSquare centeralSquare = new GridSquare();
        centeralSquare.setLocation(l);
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                //Delete old sound files
                SoundLocationWrapper wrapper = currentSoundGrid.get(i).get(j).getSoundWrapper();
                if(wrapper != null){
                    File file = new File(wrapper.getFilePath());
                    file.delete();
                }

                //Download new soundfiles
                String fileName =  Double.toString(l.getLatitude()) + Double.toString(l.getLongitude());
                File writeFile = new File(ActionCoordinator.getInstance().getAppContext().getFilesDir(), fileName);
                SoundLocationWrapper downloadedSound = new SoundLocationWrapper(writeFile.getAbsolutePath(), fileName);

                new DownloadFileAsync().execute(downloadedSound);

                int longitudeOffset = 5 - i;
                int lattitudeOffset = 5 - j;
                GridSquare updatedSquare = centeralSquare.getSquareAtOffset(longitudeOffset, lattitudeOffset);
                currentSoundGrid.get(i).get(j).setSoundWrapper(downloadedSound);
                currentSoundGrid.get(i).get(j).setLocation(updatedSquare.getLocation());
            }
        }
    }

    /** Adapted from code at http://stackoverflow.com/questions/13133498/how-to-download-mp3-file-in-android-from-a-url-and-save-it-in-sd-card-here-is **/
    class DownloadFileAsync extends AsyncTask<SoundLocationWrapper, String, String> {

        @Override
        protected String doInBackground(SoundLocationWrapper... soundLocationWrappers) {
            int count;
            try {
                SoundLocationWrapper wrapper = soundLocationWrappers[0];
                URL url = new URL(FILE_DOWNLOAD_URL_BASE + wrapper.getFileName());
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(wrapper.getFilePath());
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                //file may not exist, do I have to do anything special?
            }
            return null;
        }

    }
}
