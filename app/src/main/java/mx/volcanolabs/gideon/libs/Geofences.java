package mx.volcanolabs.gideon.libs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import mx.volcanolabs.gideon.Constants;
import mx.volcanolabs.gideon.GeofenceBroadcastReceiver;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.viewmodel.GeofenceListener;

public class Geofences {
    private GeofencingClient geofencingClient;
    private Context mContext;
    public static final String TASK_ID = "taskId";
    private GeofenceListener mListener;

    public Geofences(Context context, GeofenceListener listener) {
        geofencingClient = LocationServices.getGeofencingClient(context);
        mContext = context;
        mListener = listener;
    }

    public void addGeofenceReminder(String taskId, Location location, long expirationDuration) {
        try {
            Geofence geofence = buildGeofence(taskId, location.getLatitude(), location.getLongitude(), expirationDuration);
            geofencingClient
                    .addGeofences(buildGeofenceRequest(geofence), getPendingIntent(taskId))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mListener.onGeofenceAdded();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mListener.onGeofenceError(e);
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
                .setInitialTrigger(0)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent getPendingIntent(String taskId) {
        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        intent.putExtra(TASK_ID, taskId);
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
