package mx.volcanolabs.gideon.locations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivityLocationsBinding;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.viewmodel.LocationsViewModel;

import static mx.volcanolabs.gideon.locations.SaveLocationActivity.LOCATION_KEY;

public class LocationsActivity extends AppCompatActivity implements LocationsAdapter.LocationsActions {
    private ActivityLocationsBinding view;
    private LocationsViewModel viewModel;
    private LocationsAdapter locationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityLocationsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LocationsViewModel.class);
        locationsAdapter = new LocationsAdapter(this);
        view.rvLocations.setAdapter(locationsAdapter);
        setupListeners();
        fetchInitData();
    }

    private void fetchInitData() {
        viewModel.getLocations();
    }

    private void setupListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnAdd.setOnClickListener(v -> openSaveLocation(null));
        viewModel.getLocationsObserver().observe(this, this::onLocations);
    }

    private void openSaveLocation(Location location) {
        Intent saveLocationIntent = new Intent(this, SaveLocationActivity.class);
        saveLocationIntent.putExtra(LOCATION_KEY, location);
        startActivity(saveLocationIntent);
    }

    private void onLocations(List<Location> locationList) {
        locationsAdapter.setData(locationList);
    }

    @Override
    public void onLocationClicked(Location location) {
        openSaveLocation(location);
    }

    @Override
    public void onRemoveClicked(Location location) {
        viewModel.removeLocation(location);
    }
}