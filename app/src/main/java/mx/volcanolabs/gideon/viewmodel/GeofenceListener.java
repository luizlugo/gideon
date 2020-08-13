package mx.volcanolabs.gideon.viewmodel;

import mx.volcanolabs.gideon.libs.Geofences;

public interface GeofenceListener {
    void onGeofenceListener(Geofences.CODES code);
}
