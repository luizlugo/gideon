package mx.volcanolabs.gideon.viewmodel;

import mx.volcanolabs.gideon.libs.Geofences;
import mx.volcanolabs.gideon.models.Task;

public interface GeofenceListener {
    void onGeofenceListener(Geofences.CODES code, Task task);
}
