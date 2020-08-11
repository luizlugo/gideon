package mx.volcanolabs.gideon.groups;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivitySaveGroupBinding;
import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.viewmodel.SaveGroupViewModel;

public class SaveGroupActivity extends AppCompatActivity {
    public static final String GROUP_KEY = "group_key";
    ActivitySaveGroupBinding view;
    SaveGroupViewModel viewModel;
    private Group group;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivitySaveGroupBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(SaveGroupViewModel.class);
        setupEventListeners();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(GROUP_KEY) && bundle.getSerializable(GROUP_KEY) != null) {
            isUpdating = true;
            group = (Group) bundle.getSerializable(GROUP_KEY);
            updateExistingGroupInformation();
        }

        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.btn_save) {
            onSaveGroupClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEventListeners() {
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
            view.tilName.setError(getString(R.string.name_error));
        } else {
            view.tilName.setError(null);
        }
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }

    private void onSaveGroupClicked() {
        String name = view.etName.getText().toString();
        String note = view.etNote.getText().toString();

        if (!name.isEmpty()) {
            if (isUpdating) {
                group.setName(name);
                group.setNote(note);
                viewModel.updateGroup(group);
            } else {
                viewModel.addGroup(name, note);
            }
        } else {
            handleNameInputValidations(name);
        }
    }

    private void onGroupAdded(boolean groupAdded) {
        Toast.makeText(this, R.string.add_group_added, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateExistingGroupInformation() {
        view.etName.setText(group.getName());
        view.etNote.setText(group.getNote());
    }
}