package mx.volcanolabs.gideon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
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
        currentDate = dateFormat.format(calendar.getTime());
        adapter = new TasksListAdapter(this);
        view.rvTasks.setAdapter(adapter);
    }

    private void setupListeners() {
        view.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        navView.setNavigationItemSelectedListener(this);
        view.btnAddTask.setOnClickListener(v -> openAddTaskScreen());
        view.btnFilter.setOnClickListener(v -> onFilterClicked());
    }

    private void onTasks(List<Task> taskList) {
        adapter.setData(taskList);
    }

    private void onFilterClicked() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();
                currentDate = dateFormat.format(selectedDate);
                updateSelectedDate(dateFormatScreen.format(selectedDate));
                getTasks();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void updateSelectedDate(String date) {
        view.tvDate.setText(date);
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
        task.setCompleted(true);
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
        if (item.getItemId() == R.id.btn_todo) {
            completed = false;
        } else if (item.getItemId() == R.id.btn_completed) {
            completed = true;
        }
        getTasks();
        return super.onOptionsItemSelected(item);
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
}