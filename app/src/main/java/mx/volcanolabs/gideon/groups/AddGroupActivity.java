package mx.volcanolabs.gideon.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivityAddGroupBinding;
import mx.volcanolabs.gideon.viewmodel.AddGroupViewModel;

public class AddGroupActivity extends AppCompatActivity {
    ActivityAddGroupBinding view;
    AddGroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityAddGroupBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AddGroupViewModel.class);
        setupEventListeners();
    }

    private void setupEventListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnSaveGroup.setOnClickListener(v -> onSaveGroupClicked());
        view.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleNameInputValidations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewModel.groupsListener.observe(this, this::onGroupAdded);
    }

    private void handleNameInputValidations(String name) {
        if (name.isEmpty()) {
            view.tilName.setError(getString(R.string.add_group_name_error));
        } else {
            view.tilName.setError(null);
        }
    }

    private void onSaveGroupClicked() {
        String name = view.etName.getText().toString();
        String note = view.etNote.getText().toString();

        if (!name.isEmpty()) {
            viewModel.saveGroup(name, note);
        } else {
            handleNameInputValidations(name);
        }
    }

    private void onGroupAdded(boolean groupAdded) {
        Toast.makeText(this, R.string.add_group_added, Toast.LENGTH_SHORT).show();
        finish();
    }
}