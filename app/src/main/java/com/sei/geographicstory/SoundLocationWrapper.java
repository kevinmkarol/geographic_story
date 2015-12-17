package com.sei.geographicstory;

/**
 * Created by Kevin on 12/16/15.
 */
public class SoundLocationWrapper {
    private String fileName;
    private String partOfSpeech;
    private String filePath;
    private Integer mSoundId;
    private boolean mIsDirty;
    private boolean soundHasPlayed;

    public SoundLocationWrapper(String filePath, String fileName){
        this.fileName = fileName;
        this.filePath = filePath;
        this.mIsDirty = false;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public boolean isDirty(){
        return  mIsDirty;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getmSoundId() {
        return mSoundId;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public void setmSoundId(Integer mSoundId) {
        this.mSoundId = mSoundId;
    }
}
