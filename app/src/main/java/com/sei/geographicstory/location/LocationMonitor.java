package com.sei.geographicstory.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sei.geographicstory.ActionCoordinator;
import com.sei.geographicstory.SoundGridManager;

/**
 * Created by Kevin on 12/16/15.
 */
public class LocationMonitor {
    private GoogleApiClient mClient;
    private GridSquare lastSquare = null;
    private static final float MIN_ACCURACY = 5.0f;

    public LocationMonitor(final Context appContext){
        mClient = new GoogleApiClient.Builder(appContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                    @Override
                    public void onConnected(Bundle bundle){
                        Log.d("Google location service", "Connected");
                        makeLocationRequest();
                    }

                    @Override
                    public void onConnectionSuspended(int i){
                        Log.d("Google location service", "disconnected");
                    }

                })
                .build();
    }

    private void makeLocationRequest(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //request.setNumUpdates(1);
        request.setInterval(1000);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        GridSquare currentSquare = new GridSquare();
                        currentSquare.setLocation(location);

                        float accuracy = location.getAccuracy();
                        ActionCoordinator.getInstance().updateAccuracyDisplay(accuracy);

                        if(!(lastSquare != null && lastSquare.isSameSquare(currentSquare))){
                            if(accuracy < MIN_ACCURACY){
                                ActionCoordinator.getInstance().updateLocationDisplay(currentSquare);
                                lastSquare = currentSquare;
                                SoundGridManager soundGridManager = SoundGridManager.getInstance();
                                soundGridManager.performActionForLocation(currentSquare);
                            }
                        }
                    }
                });
    }

    public void startMonitoring(){
        mClient.connect();

    }

    public void stopMonitoring(){
        mClient.disconnect();
    }
}
