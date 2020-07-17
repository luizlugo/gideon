package mx.volcanolabs.gideon;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class GideonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
