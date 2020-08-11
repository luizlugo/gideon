package mx.volcanolabs.gideon.libs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import mx.volcanolabs.gideon.Constants;
import mx.volcanolabs.gideon.GeofenceBroadcastReceiver;
import mx.volcanolabs.gideon.models.Location;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

public class Geofences {
    private GeofencingClient geofencingClient;
    private Context mContext;

    public Geofences(Context context) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        mContext = context;
    }

    public void addGeofenceReminder(String taskKey, Location location, long expirationDuration) {
        try {
            Geofence geofence = buildGeofence(taskKey, location.getLatitude(), location.getLongitude(), expirationDuration);
            geofencingClient
                    .addGeofences(buildGeofenceRequest(geofence), getPendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // TODO: Throw error
                        }
                    });
        } catch (SecurityException exception) {
            // TODO: Throw error
        }
    }

    private Geofence buildGeofence(String taskKey, Double latitude, Double longitude, long expirationDuration) {
        return new Geofence
                .Builder()
                .setRequestId(taskKey)
                .setCircularRegion(
                        latitude,
                        longitude,
                        // TODO: Change this to be a setting customizable by user
                        // it could be either at general or task level
                        Constants.geofence_radius_in_meters
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(60000 * 5) // TODO: Change this to be configured by the user, now 5 minutes delay
                .setExpirationDuration(expirationDuration)
                .build();
    }

    private GeofencingRequest buildGeofenceRequest(Geofence geofence) {
        return new GeofencingRequest
                .Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
