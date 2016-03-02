package com.mallow.geofences;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;

/**
 * Company: Mallow Technology
 * Created by bhagya on 23/07/15.
 */
public class GLLocationHandler extends IntentService {
    private final String TAG = "BBLocationHandler";

    public GLLocationHandler() {
        super("com.mallow.geofences.GLLocationHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null) {
            Log.d(TAG, geofencingEvent.getTriggeringLocation().toString());
            Log.d(TAG, geofencingEvent.getTriggeringGeofences().toString());

            ArrayList<String> storeNames = new ArrayList<>();
            for (int i = 0; i < geofencingEvent.getTriggeringGeofences().size(); i++) {
                storeNames.add(geofencingEvent.getTriggeringGeofences().get(i).getRequestId());
            }

            for (int i = 0; i < storeNames.size(); i++) {
                String individualLocationId = storeNames.get(i);
                Log.d(TAG, "Requested Id" + individualLocationId);

                try {
                    // Get a notification builder that's compatible with platform versions >= 4
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                    // Define the notification settings.
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Alert")
                            .setContentText("You have entered " +individualLocationId);

                    // Dismiss notification once the user touches it.
                    builder.setAutoCancel(true);

                    // Get an instance of the Notification manager
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Issue the notification
                    mNotificationManager.notify(0, builder.build());
                } catch (NullPointerException exception) {
                    Log.d(TAG, "exception is"+exception.getLocalizedMessage());
                }
            }
        } else {
            Log.d(TAG, "location is null");
        }
    }
}
