package mx.volcanolabs.gideon.viewmodel;

public interface GeofenceListener {
    void onGeofenceAdded();
    void onGeofenceError(Exception exception);
    void onPermissionsError();
}
