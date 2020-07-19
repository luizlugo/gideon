package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;

public class SaveTaskViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference tasksDbReference = FirebaseDatabase.getInstance().getReference("Tasks").child(user.getUid());
    private DatabaseReference groupsDbReference = FirebaseDatabase.getInstance().getReference("Groups").child(user.getUid());
    private DatabaseReference locationsDbReference = FirebaseDatabase.getInstance().getReference("Locations").child(user.getUid());
    private MutableLiveData<List<Group>> groupsForUser = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsForUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> taskListener = new MutableLiveData<>();

    public SaveTaskViewModel(@NonNull Application application) {
        super(application);
        groupsDbReference.addValueEventListener(new GroupsValueEventListener());
        locationsDbReference.addValueEventListener(new LocationEventListener());
    }

    public LiveData<List<Group>> getGroupsForUser() {
        return groupsForUser;
    }

    public LiveData<List<Location>> getLocationsForUser() {
        return locationsForUser;
    }

    public LiveData<Boolean> getTaskListener() {
        return taskListener;
    }

    public void addTask(Task task) {
        tasksDbReference.push().setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                taskListener.postValue(true);
            }
        });
    }

    private class LocationEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Location> locations = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Location location = snapshot.getValue(Location.class);
                location.setKey(snapshot.getKey());
                locations.add(location);
            }
            locationsForUser.postValue(locations);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    private class GroupsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Group> groups = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Group group = snapshot.getValue(Group.class);
                group.setKey(snapshot.getKey());
                groups.add(group);
            }
            groupsForUser.postValue(groups);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    private class LocationsValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Location> locations = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Location location = snapshot.getValue(Location.class);
                location.setKey(snapshot.getKey());
                locations.add(location);
            }
            locationsForUser.postValue(locations);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

}
