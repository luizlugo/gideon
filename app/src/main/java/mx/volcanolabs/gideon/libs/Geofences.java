package mx.volcanolabs.gideon.libs;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import mx.volcanolabs.gideon.Constants;
import mx.volcanolabs.gideon.models.Location;

public class Geofences {
    public static void addPoint(String taskKey, Location location) {
        Geofence geofence = new Geofence
                .Builder()
                .setRequestId(taskKey)
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        // TODO: Change this to be a setting customizable by user
                        // it could be either at general or task level
                        Constants.geofence_radius_in_meters
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
    }
}
