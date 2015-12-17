package com.sei.geographicstory;

import android.content.Context;
import android.location.Location;

import com.sei.geographicstory.location.GridSquare;
import com.sei.geographicstory.location.LocationMonitor;

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


    /*  Private Constructor preventing other classes from instantiating
 *
 */
    private ActionCoordinator(){
    }

    public Context getAppContext(){
        return appContext;
    }

    public void initializeActionCoordinator(Context appContext, MainActivity activity){
        mlocationMonitor = new LocationMonitor(appContext);
        singleton.appContext = appContext;
        singleton.activity = activity;
        singleton.mAudioManager = new AudioManager(appContext);
    }

    public static ActionCoordinator getInstance(){return singleton;}

    public void startMonitoring(){
        mlocationMonitor.startMonitoring();
    }

    public  void stopMonitoring(){
        mlocationMonitor.stopMonitoring();
    }

    public void recordSoundForSquare(final SoundLocationWrapper wrapper){
        mAudioManager.toggleRecord(true, wrapper);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               mAudioManager.toggleRecord(false, wrapper);
            }
        }, 3000);
    }

    public void playSoundForSquare(SoundLocationWrapper wrapper){
        mAudioManager.play(wrapper);
    }

    public void updateLocationDisplay(Location l){
        activity.setDisplayLocation(l);
    }
}
