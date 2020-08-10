package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;
import android.media.MediaDrm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.mappers.GroupMapper;

public class GroupsViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Group>> groupsObserver = new MutableLiveData<>();
    private CollectionReference groupsReference;
    private ListenerRegistration groupsListenerRegistration;

    public GroupsViewModel(@NonNull Application application) {
        super(application);
        groupsReference = db.collection("users")
                .document(user.getUid())
                .collection("groups");
    }

    public LiveData<List<Group>> getGroupsObserver() {
        return groupsObserver;
    }

    public void deleteGroup(Group group) {
        groupsReference
                .document(group.getKey())
                .delete();
    }

    public void getGroups() {
        groupsListenerRegistration = groupsReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // TODO: Throw exception
                            return;
                        }

                        List<Group> groups = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            groups.add(GroupMapper.transform(document));
                        }
                        groupsObserver.postValue(groups);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupsReference = null;
        groupsListenerRegistration.remove();
    }
}
