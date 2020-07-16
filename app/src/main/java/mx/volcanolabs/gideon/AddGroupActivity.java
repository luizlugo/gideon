package mx.volcanolabs.gideon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import mx.volcanolabs.gideon.databinding.ActivityAddGroupBinding;

public class AddGroupActivity extends AppCompatActivity {
    ActivityAddGroupBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setupEventListeners();
        setContentView(view.getRoot());
    }

    private void setupEventListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnSaveGroup.setOnClickListener(v -> onSaveGroupClicked());
    }

    private void onSaveGroupClicked() {

    }
}