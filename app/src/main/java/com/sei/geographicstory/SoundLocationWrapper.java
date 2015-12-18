package com.sei.geographicstory;

/**
 * Created by Kevin on 12/16/15.
 */
public class SoundLocationWrapper {
    private String fileName;
    //private String partOfSpeech;
    private String filePath;
    private Integer mSoundId;
    private boolean mIsDirty;
    private boolean soundPlayed;

    public SoundLocationWrapper(String filePath, String fileName){
        this.fileName = fileName;
        this.filePath = filePath;
        this.mIsDirty = false;
        soundPlayed = false;
    }

    public String getFileName() {
        return fileName;
    }

    /**public String getPartOfSpeech() {
        return partOfSpeech;
    }

     public void setPartOfSpeech(String partOfSpeech) {
     this.partOfSpeech = partOfSpeech;
     }**/

    public boolean isDirty(){
        return  mIsDirty;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getmSoundId() {
        return mSoundId;
    }

    public boolean getSoundPlayed(){
        return soundPlayed;
    }

    public void setSoundPlayed(boolean played){
        this.soundPlayed = played;
    }

    public void setmSoundId(Integer mSoundId) {
        this.mSoundId = mSoundId;
    }

    public void setIsDirty(boolean isDirty){
        this.mIsDirty = isDirty;
    }
}
