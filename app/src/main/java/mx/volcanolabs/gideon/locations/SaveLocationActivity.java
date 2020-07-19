package mx.volcanolabs.gideon.locations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivitySaveLocationBinding;
import mx.volcanolabs.gideon.models.Location;

public class SaveLocationActivity extends AppCompatActivity {
    private ActivitySaveLocationBinding view;
    private static final int RC_PLACES = 1903;
    private Location location;
    private TextInputEditText currentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivitySaveLocationBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        setupEventListeners();
        location = new Location();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_PLACES) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();
                location.setAddress(place.getAddress());
                location.setLatitude(latLng.latitude);
                location.setLatitude(latLng.longitude);
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

    private void setupEventListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.etAddress.setOnFocusChangeListener(onFocusChangeListener);
        view.etAddress.addTextChangedListener(new TextFieldValidator());
        view.etName.setOnFocusChangeListener(onFocusChangeListener);
        view.etName.addTextChangedListener(new TextFieldValidator());
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
    }

    private class TextFieldValidator implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String currentText = currentField.getText().toString();
            if (currentText.isEmpty()) {
                handleEmptyFieldMessage();
            }
        }
    }

    private void handleEmptyFieldMessage() {
        switch (currentField.getId()) {
            case R.id.et_name:
                currentField.setText(R.string.name_error);
                break;
            case R.id.et_address:
                currentField.setText(R.string.address_error);
                break;
        }
    }

    private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.et_name:
                    currentField = view.etName;
                    break;
                case R.id.et_address:
                    currentField = view.etAddress;
                    if (hasFocus) {
                        onAddressClicked();
                        view.etAddress.clearFocus();
                    }
                    break;
            }
        }
    };
}