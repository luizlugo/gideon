package mx.volcanolabs.gideon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import mx.volcanolabs.gideon.databinding.ActivityAddGroupBinding;
import mx.volcanolabs.gideon.viewmodel.AddGroupViewModel;

public class AddGroupActivity extends AppCompatActivity {
    ActivityAddGroupBinding view;
    AddGroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setupEventListeners();
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AddGroupViewModel.class);
    }

    private void setupEventListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnSaveGroup.setOnClickListener(v -> onSaveGroupClicked());
    }

    private void onSaveGroupClicked() {
        viewModel.saveGroup(view.etName.getText().toString(), view.etNote.getText().toString());
    }
}