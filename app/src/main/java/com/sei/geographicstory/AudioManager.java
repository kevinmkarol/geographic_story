package com.sei.geographicstory;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 12/16/15.
 */
public class AudioManager {
    private static final int MAX_SOUNDS = 1;

    private SoundPool mSoundPool;
    private MediaRecorder mRecorder;


    //////////
    ///Audio playback functionality
    /////////


    public AudioManager(Context context){
        mSoundPool = new SoundPool.Builder().setMaxStreams(MAX_SOUNDS).build();
    }

    public void play(SoundLocationWrapper wrapper){
        Integer soundID = wrapper.getmSoundId();
        if(soundID == null){
            return;
        }

        mSoundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void replaceCurrentSoundPool(List<SoundLocationWrapper> newSounds){
        mSoundPool.release();
        mSoundPool = null;
        mSoundPool = new SoundPool.Builder().setMaxStreams(MAX_SOUNDS).build();
        loadSounds(newSounds);
    }

    private void loadSounds(List<SoundLocationWrapper> allSounds){
        for(SoundLocationWrapper wrapper: allSounds){
            load(wrapper);
        }
    }

    private Integer load(SoundLocationWrapper wrapper){
        int soundID = mSoundPool.load(wrapper.getFilePath(), 1);
        wrapper.setmSoundId(soundID);
        return soundID;
    }

    //////////
    ///Audio recording functionality
    /////////

    public void toggleRecord(boolean start, SoundLocationWrapper wrapper){
        if(start){
            startRecording(wrapper.getFilePath());
        }else{
            stopRecording();
            wrapper.setmSoundId(load(wrapper));
        }
    }

    private void startRecording(String filePath){
        if(mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(filePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(filePath, "prepare() failed");
            }

            mRecorder.start();
        }

    }

    private void stopRecording(){
        if(mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
}
