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

import mx.volcanolabs.gideon.databinding.ActivityMainBinding;
import mx.volcanolabs.gideon.groups.GroupsActivity;
import mx.volcanolabs.gideon.locations.LocationsActivity;
import mx.volcanolabs.gideon.models.Task;
import mx.volcanolabs.gideon.tasks.SaveTaskActivity;
import mx.volcanolabs.gideon.viewmodel.MainViewModel;

import static mx.volcanolabs.gideon.Constants.due_date_format;
import static mx.volcanolabs.gideon.Constants.due_date_format_screen;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat(due_date_format);
    private SimpleDateFormat dateFormatScreen = new SimpleDateFormat(due_date_format_screen);
    private String currentDate;
    private TasksListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = view.drawerLayout;
        navView = view.navView;
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);
        setupActionBar();
        checkAuthenticatedUser();
        setupListeners();
        currentDate = dateFormat.format(calendar.getTime());
        adapter = new TasksListAdapter();
        view.rvTasks.setAdapter(adapter);
        getTasks();
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
        viewModel.getTaskListener().observe(this, this::onTasks);
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
        viewModel.filterTasks(currentDate);
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
        updateDrawerUserName();
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