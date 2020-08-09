package mx.volcanolabs.gideon;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

import static mx.volcanolabs.gideon.Constants.places_api_key;

public class GideonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializePlaces();
    }

    private void initializePlaces() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), places_api_key);
    }
}
