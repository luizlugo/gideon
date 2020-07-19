package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

public class GroupsViewModel extends AndroidViewModel {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference groupsDbReference = FirebaseDatabase.getInstance().getReference("Groups");
    private MutableLiveData<List<Group>> groupsObserver = new MutableLiveData<>();
    private GroupsEventListener groupsEventListener = new GroupsEventListener();

    public GroupsViewModel(@NonNull Application application) {
        super(application);
        groupsDbReference.child(currentUser.getUid()).addValueEventListener(groupsEventListener);
    }

    public MutableLiveData<List<Group>> getGroupsObserver() {
        return groupsObserver;
    }

    public void deleteGroup(Group group) {
        groupsDbReference.child(currentUser.getUid()).child(group.getKey()).removeValue();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupsDbReference.child(currentUser.getUid()).removeEventListener(groupsEventListener);
    }

    private class GroupsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Group> groups = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Group group = snapshot.getValue(Group.class);
                group.setKey(snapshot.getKey());
                groups.add(group);
            }
            groupsObserver.postValue(groups);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }
}
