package mx.volcanolabs.gideon.locations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivitySaveLocationBinding;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.viewmodel.SaveLocationViewModel;

import static mx.volcanolabs.gideon.Constants.places_api_key;

public class SaveLocationActivity extends AppCompatActivity {
    public static final String LOCATION_KEY = "location_key";
    private ActivitySaveLocationBinding view;
    private static final int RC_PLACES = 1903;
    private Location location;
    private TextInputLayout currentField;
    private SaveLocationViewModel viewModel;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivitySaveLocationBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SaveLocationViewModel.class);
        setupEventListeners();

        Bundle params = getIntent().getExtras();
        if (params != null && params.getSerializable(LOCATION_KEY) != null) {
            isUpdating = true;
            location = (Location) params.getSerializable(LOCATION_KEY);
            populateLocationData();
        } else {
            location = new Location();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_PLACES) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();
                location.setAddress(place.getAddress());
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                view.etAddress.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateLocationData() {
        view.etName.setText(location.getName());
        view.etAddress.setText(location.getAddress());
        view.etNote.setText(location.getNote());
        view.chxDefaultLocation.setChecked(location.isDefaultLocation());
    }

    private void setupEventListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnSave.setOnClickListener(v -> onSaveClicked());
        view.etAddress.setOnFocusChangeListener(onFocusChangeListener);
        view.etAddress.addTextChangedListener(new TextFieldValidator());
        view.etName.setOnFocusChangeListener(onFocusChangeListener);
        view.etName.addTextChangedListener(new TextFieldValidator());
        viewModel.getLocationsObserver().observe(this, this::onLocationSaved);
    }

    private void onLocationSaved(Boolean saved) {
        if (saved) {
            Toast.makeText(this, R.string.location_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.location_save_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void onAddressClicked() {                   
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        List<String> countries = new ArrayList<>();
        countries.add("US");
        countries.add("MX");

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countries)
                .setInitialQuery(location.getAddress() != null ? location.getAddress() : "")
                .build(this);
        startActivityForResult(intent, RC_PLACES);
    }

    private void onSaveClicked() {
        String name = view.etName.getText().toString();
        String address = view.etAddress.getText().toString();
        boolean emptyError = false;

        if (name.isEmpty()) {
            displayEmptyNameError();
            emptyError = true;
        }

        if (address.isEmpty()) {
            displayAddressEmptyError();
            emptyError = true;
        }

        if (!emptyError) {
            location.setName(view.etName.getText().toString());
            location.setNote(view.etNote.getText().toString());
            location.setDefaultLocation(view.chxDefaultLocation.isChecked());

            if (!isUpdating) {
                viewModel.addLocation(location);
            } else {
                viewModel.updateLocation(location);
            }
        }
    }

    private class TextFieldValidator implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable text) {
            String currentText = text.toString();

            if (currentField != null) {
                currentField.setError(null);
            }

            if (currentText.isEmpty()) {
                handleEmptyFieldMessage();
            }
        }
    }

    private void handleEmptyFieldMessage() {
        switch (currentField.getId()) {
            case R.id.til_name:
                displayEmptyNameError();
                break;
            case R.id.til_address:
                displayAddressEmptyError();
                break;
        }
    }

    private void displayEmptyNameError() {
        view.tilName.setError(getString(R.string.name_error));
    }

    private void displayAddressEmptyError() {
        view.tilAddress.setError(getString(R.string.address_error));
    }

    private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.et_name:
                    currentField = view.tilName;
                    break;
                case R.id.et_address:
                    currentField = view.tilAddress;
                    if (hasFocus) {
                        onAddressClicked();
                        view.etAddress.clearFocus();
                    }
                    break;
            }
        }
    };
}