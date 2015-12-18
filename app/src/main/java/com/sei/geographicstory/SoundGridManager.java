package com.sei.geographicstory;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.sei.geographicstory.location.GridSquare;

import org.apache.http.client.HttpClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 12/16/15.
 */
public class SoundGridManager {
    private static SoundGridManager singleton = new SoundGridManager();

    //File upload and download constants
    private static String FILE_UPLOAD_URL = "http://kevinmkarol.com:1217/FileUploader/uploadMP3";
    private static String FILE_DOWNLOAD_URL_BASE = "http://kevinmkarol.com:1217/FileUploader/downloadMP3/";
    private static String lineEnd = "\r\n";
    private static String twoHyphens = "--";
    private static String boundary = "AaB03x87yxdkjnxvi7";
    private static String name = "geoMP3";

    //Internal Grid Information
    private ArrayList<ArrayList<GridSquare>> currentSoundGrid;
    private static final int gridSize = 10; //Must be even
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

    public void performActionForLocation(final GridSquare square){
        findSquareInGrid(square);
        //Next update will cause the action to be performed
        if(currentXIndex != -1 && currentYIndex != -1) {
            SoundLocationWrapper soundAtSquare = currentSoundGrid.get(currentYIndex)
                    .get(currentXIndex).getSoundWrapper();

            Random rand = new Random();
            int randomNum = rand.nextInt(randomRange);

            //Determine what action to perform for square
            File possibleSoundFile = new File(soundAtSquare.getFilePath());
            File parentDir = new File("/data/data/com.sei.geographicstory/files/");
            for(String fileName : parentDir.list()){
                    Log.d("FileName", fileName);
            }

            if(!possibleSoundFile.exists() || randomNum < oddsOfNewWord) {
                ActionCoordinator.getInstance().recordSoundForSquare(soundAtSquare);
            } else {
                ActionCoordinator.getInstance().playSoundForSquare(soundAtSquare);
            }
        }else{
            /**new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    performActionForLocation(square);
                }
            }, 1000);**/
        }
    }

    private void updateSquare(int xCord, int yCord, SoundLocationWrapper wrapper, Location location){
        currentSoundGrid.get(yCord).get(xCord).setLocation(location);
        currentSoundGrid.get(yCord).get(xCord).setSoundWrapper(wrapper);
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
            downloadGridAtLocation(square);
        }
    }

    //make this private again after testing
    public void uploadDirtySoundFiles(){
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                SoundLocationWrapper wrapper = currentSoundGrid.get(i).get(j).getSoundWrapper();
                if(wrapper != null && wrapper.isDirty()){
                    try {
                        uploadFile(wrapper);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /* code adapted from http://stackoverflow.com/questions/4966910/androidhow-to-upload-mp3-file-to-http-server*/
    private void uploadFile(SoundLocationWrapper wrapper) throws IOException{
        new UploadFileAsync().execute(wrapper);
    }

    private class UploadFileAsync extends AsyncTask<SoundLocationWrapper, String, Void>{


        @Override
        protected Void doInBackground(SoundLocationWrapper... params){

            SoundLocationWrapper wrapper = params[0];
            String path = wrapper.getFilePath();
            FileInputStream fileInputStream = null;
            HttpURLConnection conn = null;
            DataInputStream dis = null;
            DataOutputStream dos = null;

            try {
                File f = new File(path);

                fileInputStream = new FileInputStream(f);

                //this wouldn't be a good idea of files weren't known to be very small
                byte fileContent[] = new byte[(int) f.length()];
                fileInputStream.read(fileContent);

                URL url = new URL(FILE_UPLOAD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setDoInput(true);
                conn.connect();

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                               + "\";filename=\"" + wrapper.getFileName() + "\"" + lineEnd );
                dos.writeBytes(lineEnd);
                dos.write(fileContent);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                dos.flush();
                dos.close();

                //read response to send data
                dis = new DataInputStream(conn.getInputStream());


            }catch(Exception e){
                e.printStackTrace();
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }

            return null;
        }
    }

    private void downloadGridAtLocation(GridSquare square){
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                //Delete old sound files
                SoundLocationWrapper wrapper = currentSoundGrid.get(i).get(j).getSoundWrapper();
                if(wrapper != null){
                    File file = new File(wrapper.getFilePath());
                    file.delete();
                }

                int longitudeOffset = gridSize/2 - i;
                int lattitudeOffset = gridSize/2 - j;
                GridSquare updatedSquare = square.getSquareAtOffset(longitudeOffset, lattitudeOffset);

                //Download new soundfiles
                String fileName =  updatedSquare.getStringRepresentation();
                File writeFile = new File(ActionCoordinator.getInstance().getAppContext().getFilesDir(), fileName);
                SoundLocationWrapper downloadedSound = new SoundLocationWrapper(writeFile.getAbsolutePath(), fileName);

                updatedSquare.setSoundWrapper(downloadedSound);

                currentSoundGrid.get(i).set(j, updatedSquare);

                new DownloadFileAsync().execute(downloadedSound);
            }
        }
    }

    /** Adapted from code at http://stackoverflow.com/questions/13133498/how-to-download-mp3-file-in-android-from-a-url-and-save-it-in-sd-card-here-is **/
    private class DownloadFileAsync extends AsyncTask<SoundLocationWrapper, String, Void> {

        @Override
        protected Void doInBackground(SoundLocationWrapper... soundLocationWrappers) {
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
                e.printStackTrace();
            }
            return null;
        }

    }
}
