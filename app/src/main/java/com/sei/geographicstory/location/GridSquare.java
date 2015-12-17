package com.sei.geographicstory.location;

import android.location.Location;
import android.util.Log;

import com.sei.geographicstory.SoundLocationWrapper;

/**
 * Created by Kevin on 12/16/15.
 */
public class GridSquare {
    private static final Double squarePrecision = 0.000001;

    Location location;
    SoundLocationWrapper soundWrapper;

    public GridSquare(){
        soundWrapper = null;
        location = new Location("System Generated");
    }

    public double getLatitude(){
        return location.getLatitude();
    }

    public double getLongitude(){
        return location.getLongitude();
    }

    public Location getLocation(){
        return location;
    }

    public SoundLocationWrapper getSoundWrapper(){
        return soundWrapper;
    }
    public void setSoundWrapper(SoundLocationWrapper wrapper){
        this.soundWrapper = wrapper;
    }

    public void setLocation(Location l){
        this.location = l;
    }


    public boolean isSameSquare(Object other){
        GridSquare compareTo = (GridSquare) other;
        double longitudeDifference = Math.abs(compareTo.getLongitude() - location.getLongitude());
        double lattitudeDifference = Math.abs(compareTo.getLatitude() - location.getLatitude());

        if(longitudeDifference > squarePrecision
                || lattitudeDifference > squarePrecision){
            return false;
        }

        return true;
    }

    public GridSquare getSquareAtOffset(int longitudeOffset, int lattitudeOffset){
        GridSquare offsetSquare = new GridSquare();
        Location l  = new Location("System Generated");
        l.setLatitude(this.getLatitude() + (lattitudeOffset * squarePrecision));
        l.setLongitude(this.getLongitude() + (longitudeOffset * squarePrecision));
        offsetSquare.setLocation(l);
        return offsetSquare;
    }
}
