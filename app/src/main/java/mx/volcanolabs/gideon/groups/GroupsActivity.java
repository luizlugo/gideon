package mx.volcanolabs.gideon.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import mx.volcanolabs.gideon.databinding.ActivityGroupsBinding;
import mx.volcanolabs.gideon.groups.AddGroupActivity;

public class GroupsActivity extends AppCompatActivity {
    ActivityGroupsBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityGroupsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        view.btnBack.setOnClickListener(v -> finish());
        view.btnAdd.setOnClickListener(v -> openAddGroupScreen());
        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }

    private void openAddGroupScreen() {
        Intent addGroup = new Intent(this, AddGroupActivity.class);
        startActivity(addGroup);
    }


}