package mx.volcanolabs.gideon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import mx.volcanolabs.gideon.databinding.ActivityGroupsBinding;
import mx.volcanolabs.gideon.databinding.ActivityMainBinding;

public class GroupsActivity extends AppCompatActivity {
    ActivityGroupsBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityGroupsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        view.btnBack.setOnClickListener(v -> finish());
        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }
}