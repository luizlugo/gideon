package mx.volcanolabs.gideon.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.databinding.ActivitySaveTaskBinding;
import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;
import mx.volcanolabs.gideon.viewmodel.SaveTaskViewModel;

import static mx.volcanolabs.gideon.Constants.due_date_format;
import static mx.volcanolabs.gideon.Constants.due_date_format_screen;

public class SaveTaskActivity extends AppCompatActivity {
    private ActivitySaveTaskBinding view;
    private SaveTaskViewModel viewModel;
    private TaskOnFocusChange taskOnFocusChange = new TaskOnFocusChange();
    private TextInputLayout currentField;
    private final Calendar calendar = Calendar.getInstance();
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private final int year = calendar.get(Calendar.YEAR);
    private Task task = new Task();
    private SimpleDateFormat dateFormatScreen = new SimpleDateFormat(due_date_format_screen, Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat(due_date_format, Locale.US);
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivitySaveTaskBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SaveTaskViewModel.class);
        setupListeners();
        setupPriorities();
        fetchInitData();
    }

    private void fetchInitData() {
        viewModel.getGroups();
        viewModel.getLocations();
    }

    private void setupListeners() {
        view.btnBack.setOnClickListener(v -> finish());
        view.btnSave.setOnClickListener(v -> onSaveClicked());
        view.etDescription.setOnFocusChangeListener(taskOnFocusChange);
        view.etDescription.addTextChangedListener(new TaskTextWatcher());
        view.etDueDate.setOnFocusChangeListener(taskOnFocusChange);
        viewModel.getGroupsForUser().observe(this, this::onGroupsArrived);
        viewModel.getLocationsForUser().observe(this, this::onLocationsArrived);
        viewModel.getTaskListener().observe(this, this::onTaskAddedSuccessfully);
    }

    private void onTaskAddedSuccessfully(Boolean added) {
        finish();
        Toast.makeText(this, R.string.task_added, Toast.LENGTH_SHORT).show();
    }

    private void onSaveClicked() {
        boolean hasError = false;
        String description = view.etDescription.getText() != null ? view.etDescription.getText().toString() : "";
        String dueDate = view.etDueDate.getText() != null ? view.etDueDate.getText().toString() : "";
        String priority = view.filledPriorityExposedDropdown.getText() != null ? view.filledPriorityExposedDropdown.getText().toString() : "";
        String group = view.filledGroupExposedDropdown.getText() != null ? view.filledGroupExposedDropdown.getText().toString() : "";

        if (description.isEmpty()) {
            displayDescriptionEmptyError();
            hasError = true;
        } else {
            task.setDescription(description);
        }

        if (dueDate.isEmpty()) {
            displayDueDateEmptyError();
            hasError = true;
        }

        if (priority.isEmpty()) {
            displayPriorityEmptyError();
            hasError = true;
        }

        if (group.isEmpty()) {
            displayGroupEmptyError();
            hasError = true;
        }

        if (!validLocationFields()) {
            displayLocationError();
            hasError = true;
        }

        task.setGeofenceActive(view.chxDefaultLocation.isChecked());
        task.setCompleted(false);

        if (!hasError && !isUpdating) {
            viewModel.addTask(task);
        }
    }

    private boolean validLocationFields() {
        boolean isLocationCombinationValid = true;
        String location = view.filledLocationExposedDropdown.getText().toString();

        if (view.chxDefaultLocation.isChecked() && location.isEmpty()) {
            isLocationCombinationValid = false;
        }

        return isLocationCombinationValid;

    }

    private void displayLocationError() {
        view.tilLocation.setError(getString(R.string.location_error));
    }

    private void displayDescriptionEmptyError() {
        view.tilDescription.setError(getString(R.string.description_error));
    }

    private void displayDueDateEmptyError() {
        view.tilDueDate.setError(getString(R.string.due_date_error));
    }

    private void displayPriorityEmptyError() {
        view.tilPriority.setError(getString(R.string.priority_error));
    }

    private void displayGroupEmptyError() {
        view.tilGroup.setError(getString(R.string.group_error));
    }

    private void onGroupsArrived(List<Group> groupList) {
        ArrayAdapter<Group> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                groupList
        );
        AutoCompleteTextView groupsDropDown = view.filledGroupExposedDropdown;
        groupsDropDown.setAdapter(adapter);
        groupsDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Group group = (Group) parent.getItemAtPosition(position);
                task.setGroup(group);
                view.tilGroup.setError(null);
            }
        });
    }

    private void onLocationsArrived(List<Location> locationList) {
        ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                locationList
        );
        AutoCompleteTextView locationsDropDown = view.filledLocationExposedDropdown;
        locationsDropDown.setAdapter(adapter);
        locationsDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Location location = (Location) parent.getItemAtPosition(position);
                task.setLocation(location);
                view.tilLocation.setError(null);
            }
        });
    }

    private void setupPriorities() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AutoCompleteTextView editTextFilledExposedDropdown = view.filledPriorityExposedDropdown;
        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String priority = (String) parent.getItemAtPosition(position);
                task.setPriority(priority);
                view.tilPriority.setError(null);
            }
        });
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                task.setDueDate(dateFormat.format(calendar.getTime()));
                updateDueDate(calendar.getTime());
                view.tilDueDate.setError(null);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDueDate(Date date) {
        view.etDueDate.setText(dateFormatScreen.format(date));
    }

    private class TaskTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String currentText = currentField.getEditText().getText().toString();
            currentField.setError(null);

            if (currentText.isEmpty() && currentField.getId() == R.id.til_description) {
                currentField.setError(getString(R.string.description_error));
            }
        }
    }

    private class TaskOnFocusChange implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.et_description:
                    currentField = view.tilDescription;
                    break;
                case R.id.et_due_date:
                    currentField = view.tilDueDate;
                    if (hasFocus) {
                        openDatePicker();
                        view.etDueDate.clearFocus();
                    }
                    break;
            }
        }
    }
}