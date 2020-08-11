package mx.volcanolabs.gideon.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.databinding.ActivityGroupsBinding;
import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.viewmodel.GroupsViewModel;

public class GroupsActivity extends AppCompatActivity implements GroupsListAdapter.GroupActions {
    ActivityGroupsBinding view;
    GroupsViewModel viewModel;
    private List<Group> groupList = new ArrayList<>();
    private GroupsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityGroupsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        setupActionBar();
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(GroupsViewModel.class);
        adapter = new GroupsListAdapter(this);
        view.rvGroups.setAdapter(adapter);

        setupListeners();
        fetchInitData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchInitData() {
        viewModel.getGroups();
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }

    private void openAddGroupScreen(Group group) {
        Intent addGroup = new Intent(this, SaveGroupActivity.class);
        addGroup.putExtra(SaveGroupActivity.GROUP_KEY, group);
        startActivity(addGroup);
    }

    private void setupListeners() {
        viewModel.getGroupsObserver().observe(this, this::onGroupAdded);
        // view.btnBack.setOnClickListener(v -> finish());
        view.btnAdd.setOnClickListener(v -> openAddGroupScreen(null));
    }

    private void onGroupAdded(List<Group> groups) {
        groupList = new ArrayList<>();
        groupList.addAll(groups);
        adapter.setData(groupList);
    }

    @Override
    public void onGroupRemoveClicked(Group group) {
        viewModel.deleteGroup(group);
    }

    @Override
    public void onGroupClicked(Group group) {
        openAddGroupScreen(group);
    }
}