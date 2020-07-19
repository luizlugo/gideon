package mx.volcanolabs.gideon.locations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivityLocationsBinding;

public class LocationsActivity extends AppCompatActivity {
    ActivityLocationsBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityLocationsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnAdd.setOnClickListener(v -> openSaveLocation());
    }

    private void openSaveLocation() {
        Intent saveLocationIntent = new Intent(this, SaveLocationActivity.class);
        startActivity(saveLocationIntent);
    }
}