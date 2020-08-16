package mx.volcanolabs.gideon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import mx.volcanolabs.gideon.services.GeofenceService;
import timber.log.Timber;

import static mx.volcanolabs.gideon.Constants.TASK_ID;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Timber.e(errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for (Geofence geofence : triggeringGeofences) {
                Intent geofenceService = new Intent(context, GeofenceService.class);
                geofenceService.putExtra(TASK_ID, geofence.getRequestId());
                GeofenceService.enqueueWork(context, geofenceService);
            }
        } else {
            Timber.e(context.getString(R.string.triggering_geofence_error_broadcast_receiver, geofenceTransition));
        }

    }
}
