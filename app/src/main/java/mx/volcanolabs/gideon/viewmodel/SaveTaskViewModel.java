package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.libs.Geofences;
import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;

public class SaveTaskViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupsReference;
    private CollectionReference locationReference;
    private CollectionReference taskReference;
    private MutableLiveData<List<Group>> groupsForUser = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsForUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> taskListener = new MutableLiveData<>();

    public SaveTaskViewModel(@NonNull Application application) {
        super(application);
        groupsReference = db.collection("users")
                .document(user.getUid())
                .collection("groups");
        locationReference = db.collection("users")
                .document(user.getUid())
                .collection("locations");
        taskReference = db.collection("tasks");
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
        task.setUserId(user.getUid());

        taskReference
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        task.setKey(documentReference.getId());

                        if (task.isGeofenceActive()) {
                            addGeofenceForTask(task);
                        }

                        taskListener.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO: Throw error
                    }
                });
    }

    private void addGeofenceForTask(Task task) {
        Geofences.addPoint(task.getKey(), task.getLocation(), calculateDuration(task.getDueDate()));
    }

    private long calculateDuration(String dueDate) {
        // TODO: Calculate the expire time duration base on the due date of the task
        return Geofence.NEVER_EXPIRE;
    }

    public void getGroups() {
        groupsReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Group> groups = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Group group = document.toObject(Group.class);
                                group.setKey(document.getId());
                                groups.add(group);
                            }
                            groupsForUser.postValue(groups);
                        } else {
                            // TODO: Throw error
                        }
                    }
                });
    }

    public void getLocations() {
        locationReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Location> locations = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Location location = document.toObject(Location.class);
                                location.setKey(document.getId());
                                locations.add(location);
                            }
                            locationsForUser.postValue(locations);
                        } else {
                            // TODO: Throw error
                        }
                    }
                });
    }
}
