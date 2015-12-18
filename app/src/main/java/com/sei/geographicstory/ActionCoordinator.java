package com.sei.geographicstory;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.location.Location;

import com.sei.geographicstory.location.GridSquare;
import com.sei.geographicstory.location.LocationMonitor;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 12/17/15.
 */
public class ActionCoordinator {
    private static ActionCoordinator singleton = new ActionCoordinator();
    private LocationMonitor mlocationMonitor;
    private AudioManager mAudioManager;

    private MainActivity activity;
    private Context appContext;

    private Integer startID;
    private Integer endID;

    private GridSquare initialSquare;

    /*  Private Constructor preventing other classes from instantiating
 *
 */
    private ActionCoordinator(){
        initialSquare = null;
        startID = 0;
        endID = 0;
    }

    public Context getAppContext(){
        return appContext;
    }

    public void initializeActionCoordinator(Context appContext, MainActivity activity){
        mlocationMonitor = new LocationMonitor(appContext);
        singleton.appContext = appContext;
        singleton.activity = activity;
        singleton.mAudioManager = new AudioManager(appContext);
        try {
            startID = mAudioManager.loadAssetDescriptor(appContext.getAssets().openFd("sound/start_tone.wav"));
            endID = mAudioManager.loadAssetDescriptor(appContext.getAssets().openFd("sound/end_tone.wav"));
        }catch (Exception e){
            e.printStackTrace();
        }
        File parentDir = new File("/data/data/com.sei.geographicstory/files/");
        for(String fileName : parentDir.list()){
            File erase = new File(parentDir + fileName);
            erase.delete();
        }

    }

    public static ActionCoordinator getInstance(){return singleton;}

    public void startMonitoring(){
        mlocationMonitor.startMonitoring();
    }

    public  void stopMonitoring(){
        mlocationMonitor.stopMonitoring();
    }

    public void recordSoundForSquare(final SoundLocationWrapper wrapper){

        mAudioManager.playSoundID(startID);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mAudioManager.toggleRecord(true, wrapper);
            }
        }, 1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mAudioManager.toggleRecord(false, wrapper);
                mAudioManager.playSoundID(endID);
                //SoundGridManager.getInstance().uploadDirtySoundFiles();
            }
        }, 4000);

    }

    public void playSoundForSquare(SoundLocationWrapper wrapper){
        mAudioManager.play(wrapper);
    }

    public void updateLocationDisplay(GridSquare square){
        if(initialSquare == null){
            initialSquare = square;
        }
        activity.updateLocationOffset(initialSquare.getSquareOffset(square));
    }

    public void updateAccuracyDisplay(float accuracy){
        activity.updateAccuracy(accuracy);
    }
}
