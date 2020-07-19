package mx.volcanolabs.gideon;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.FirebaseDatabase;

import static mx.volcanolabs.gideon.Constants.places_api_key;

public class GideonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        initializePlaces();
    }

    private void initializePlaces() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), places_api_key);
    }
}
