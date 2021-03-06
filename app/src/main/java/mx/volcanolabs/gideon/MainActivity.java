package mx.volcanolabs.gideon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.volcanolabs.gideon.databinding.ActivityMainBinding;
import mx.volcanolabs.gideon.groups.GroupsActivity;
import mx.volcanolabs.gideon.locations.LocationsActivity;
import mx.volcanolabs.gideon.models.Task;
import mx.volcanolabs.gideon.tasks.SaveTaskActivity;
import mx.volcanolabs.gideon.viewmodel.MainViewModel;
import timber.log.Timber;

import static mx.volcanolabs.gideon.Constants.DEFAULT_DATE_KEY;
import static mx.volcanolabs.gideon.Constants.due_date_format;
import static mx.volcanolabs.gideon.Constants.due_date_format_screen;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TasksListAdapter.TasksActions {
    private MainViewModel viewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Menu menu;
    private ActivityMainBinding view;
    // Firebase authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private final int RC_SIGN_IN = 1903;
    private FirebaseUser user;
    private final Calendar calendar = Calendar.getInstance();
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private final int year = calendar.get(Calendar.YEAR);
    private SimpleDateFormat dateFormat = new SimpleDateFormat(due_date_format, Locale.US);
    private SimpleDateFormat dateFormatScreen = new SimpleDateFormat(due_date_format_screen, Locale.US);
    private String currentDate;
    private TasksListAdapter adapter;
    private boolean completed = false;
    private boolean locationDialogDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = view.drawerLayout;
        navView = view.navView;
        setupActionBar();
        checkAuthenticatedUser();
        setupListeners();

        Intent intent = getIntent();
        if (intent.getStringExtra(DEFAULT_DATE_KEY) != null && !intent.getStringExtra(DEFAULT_DATE_KEY).isEmpty()) {
            handleDefaultDate(intent.getStringExtra(DEFAULT_DATE_KEY));
        } else {
            currentDate = dateFormat.format(calendar.getTime());
        }

        adapter = new TasksListAdapter(this);
        view.rvTasks.setAdapter(adapter);
    }

    private void handleDefaultDate(String date) {
        try {
            currentDate = date;
            Date defaultDate = dateFormat.parse(currentDate);
            updateSelectedDate(dateFormatScreen.format(currentDate));
        } catch (ParseException e) {
            Timber.e(e);
        }
    }

    private void setupListeners() {
        navView.setNavigationItemSelectedListener(this);
        view.btnAddTask.setOnClickListener(v -> openAddTaskScreen());
    }

    private void handleSideMenu() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void onTasks(List<Task> taskList) {
        if (taskList.size() > 0) {
            view.rvTasks.setVisibility(View.VISIBLE);
            view.vgEmptyContent.setVisibility(View.GONE);
            adapter.setData(taskList);
        } else {
            view.rvTasks.setVisibility(View.GONE);
            view.vgEmptyContent.setVisibility(View.VISIBLE);
            displayEmptyText();
        }
    }

    private void displayEmptyText() {
        if (completed) {
            view.tvEmptyMessage.setText(R.string.no_tasks_completed);
        } else {
            view.tvEmptyMessage.setText(R.string.no_tasks_todo);
        }
    }

    private void onFilterClicked() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();
                currentDate = dateFormat.format(selectedDate);
                updateSelectedDate(dateFormatScreen.format(selectedDate));
                completed = false;
                getTasks();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void updateSelectedDate(String date) {
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setTitle(date);
        }
    }

    private void getTasks() {
        viewModel.filterTasks(currentDate, completed);
    }

    private void openAddTaskScreen() {
        Intent addTaskIntent = new Intent(this, SaveTaskActivity.class);
        startActivity(addTaskIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_group:
                openGroupsScreen();
                break;
            case R.id.btn_locations:
                openLocationsScreen();
                break;
        }

        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onTaskCompletedClicked(Task task) {
        task.setCompleted(!task.isCompleted());
        viewModel.updateTask(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleSideMenu();
        } else if (item.getItemId() == R.id.filter_date) {
            onFilterClicked();
        } else if (item.getItemId() == R.id.filter_status) {
            showAlertDialogFilterStatus();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialogFilterStatus() {
        int selectedOption = completed ? 1 : 0;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.filter_tasks_by_status);
        alertDialog.setSingleChoiceItems(getResources().getStringArray(R.array.status_array), selectedOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                completed = (which != 0);
                getTasks();
                dialog.dismiss();
            }
        });
        alertDialog.create().show();
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }

    private void checkAuthenticatedUser() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    onSignInInitialized();
                } else {
                    openSignInFlow();
                }
            }
        };
    }

    private void openSignInFlow() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.app_logo)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                )
                        )
                        .build(),
                RC_SIGN_IN
        );
    }

    private void onSignInInitialized() {
        initViewModel();
        updateDrawerUserName();
        // Fetch initial tasks
        getTasks();
        checkLocationPermissions();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);
        viewModel.getTaskListener().observe(this, this::onTasks);
        viewModel.getTaskSourceChanged().observe(this, this::doRefresh);
    }

    private void doRefresh(Boolean refresh) {
        getTasks();
    }

    private void updateDrawerUserName() {
        View header = navView.getHeaderView(0);
        TextView tvName = header.findViewById(R.id.tv_user_name);
        tvName.setText(user.getDisplayName());
    }

    private void openGroupsScreen() {
        Intent groupsIntent = new Intent(this, GroupsActivity.class);
        startActivity(groupsIntent);
    }

    private void openLocationsScreen() {
        Intent groupsIntent = new Intent(this, LocationsActivity.class);
        startActivity(groupsIntent);
    }

    private void checkLocationPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied() && !locationDialogDisplayed) {
                            displayGoToSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        displayPermissionNotGrantedError(permissionToken);
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        Timber.e(dexterError.toString());
                    }
                })
                .check();
    }

    private void displayPermissionNotGrantedError(PermissionToken permissionToken) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.permissions_not_allowed_error_title)
                .setMessage(R.string.permissions_not_allowed_error_description)
                .setPositiveButton(R.string.allow, (dialog, which) -> {
                    permissionToken.continuePermissionRequest();
                    dialog.dismiss();
                })
                .show();
    }

    private void displayGoToSettingsDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.permissions_not_allowed_error_title)
                .setMessage(R.string.permissions_not_allowed_error_description)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                })
                .show();
        locationDialogDisplayed = true;
    }


}