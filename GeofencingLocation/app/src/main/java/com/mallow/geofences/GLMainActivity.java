package com.mallow.geofences;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GLMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "GLMainActivity";
    private PendingIntent pendingIntent;
    private ArrayList<Geofence> geoFences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_activity_main);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(GLMainActivity.this, 0,
                    new Intent(GLMainActivity.this, GLLocationHandler.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Button btnStart = (Button) findViewById(R.id.btn_start);
        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGeoFences(0.0, 0.0, "Location");
                //Specify your geofence location. Also specify your location title. You can add many number of geofences
                startGeofencingUpdates();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGeoFences();
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void addGeoFences(Double latitude, Double longitude, String storeName) {
        try {
            geoFences.add(new Geofence.Builder()
                    .setRequestId(storeName)
                    .setCircularRegion(latitude, longitude, 5000)//latitude,longitude,radius in meters
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)//show the alert when it reaches the region
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)//set the duration to check the region
                    .build());
        } catch (NullPointerException exception) {
            Log.d(TAG, exception.getLocalizedMessage());
        }
    }

    private void startGeofencingUpdates() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient, geoFences, pendingIntent);
            Log.d(TAG, "Location update started ..............: ");
        } catch (Exception exception) {
            Log.d(TAG, exception.getLocalizedMessage());
        }
    }

    private synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(GLMainActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception exception) {//generic exception
            Log.d(TAG, exception.getLocalizedMessage());
        }
        buildLocationUpdates();
    }

    /**
     * Method to enable the location updates
     */
    private void buildLocationUpdates() {
        try {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        } catch (Exception exception) {
            Log.d(TAG, exception.getLocalizedMessage());
        }
    }

    private void removeGeoFences() {
        try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, pendingIntent);
            Log.d(TAG, "Location update removed");
        } catch (Exception exception) {
            Log.d(TAG, exception.getLocalizedMessage());
        }
    }
}
