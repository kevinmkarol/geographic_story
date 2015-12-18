package com.sei.geographicstory.location;

/**
 * Created by Kevin on 12/17/15.
 */
public class OffsetWrapper {
    private long lattitudeOffset;
    private long longitudeOffset;

    public OffsetWrapper(long lattitudeOffset, long longitudeOffset){
        this.lattitudeOffset = lattitudeOffset;
        this.longitudeOffset = longitudeOffset;
    }

    public long getLattitudeOffset(){
        return lattitudeOffset;
    }

    public long getLongitudeOffset(){
        return longitudeOffset;
    }

}
