package com.sei.geographicstory.location;

import android.location.Location;

import com.sei.geographicstory.SoundLocationWrapper;

import java.io.ObjectInputStream;

/**
 * Created by Kevin on 12/16/15.
 */
public class GridSquare {
    //private static final Double squarePrecision = 0.00001;
    private static final Integer PRECISION_MULTIPLIER = 10000;

    private long mlatitude;
    private long mlongitude;
    //Location location;
    SoundLocationWrapper soundWrapper;

    public GridSquare(){
        soundWrapper = null;
        mlongitude = 0;
        mlatitude = 0;
        //location = new Location("System Generated");
    }

    public long getLatitude(){
        return mlatitude;
    }

    public long getLongitude(){
        return mlongitude;
    }

    public String getStringRepresentation(){
        return Long.toString(mlatitude) + Long.toString(mlongitude);
    }

    /**public Location getLocation(){
        return location;
    }**/

    public SoundLocationWrapper getSoundWrapper(){
        return soundWrapper;
    }
    public void setSoundWrapper(SoundLocationWrapper wrapper){
        this.soundWrapper = wrapper;
    }

    public void setLocation(Location l){
        mlatitude = (long) (l.getLatitude() * PRECISION_MULTIPLIER);
        mlongitude = (long) (l.getLongitude() * PRECISION_MULTIPLIER);
    }

    public void setLocation(long lattitude, long longitude){
        this.mlongitude = longitude;
        this.mlatitude = lattitude;
    }


    public boolean isSameSquare(Object other){
        GridSquare compareTo = (GridSquare) other;
        long longitudeDifference = compareTo.getLongitude() - mlongitude;
        long lattitudeDifference = compareTo.getLatitude() - mlatitude;

        if(longitudeDifference != 0
                || lattitudeDifference != 0){
            return false;
        }

        return true;
    }

    public GridSquare getSquareAtOffset(int longitudeOffset, int lattitudeOffset){
        GridSquare offsetSquare = new GridSquare();
        offsetSquare.setLocation(this.getLatitude() + lattitudeOffset, this.getLongitude() + longitudeOffset);
        return offsetSquare;
    }

    public OffsetWrapper getSquareOffset(Object other){
        GridSquare compareTo = (GridSquare) other;
        long longitudeDifference = compareTo.getLongitude() - mlongitude;
        long lattitudeDifference = compareTo.getLatitude() - mlatitude;

        return new OffsetWrapper(lattitudeDifference, longitudeDifference);
    }

}
