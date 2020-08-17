package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
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

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.volcanolabs.gideon.libs.Geofences;
import mx.volcanolabs.gideon.libs.Utils;
import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;

import static mx.volcanolabs.gideon.Constants.due_date_format;
import static mx.volcanolabs.gideon.Constants.due_date_time_format;

public class SaveTaskViewModel extends AndroidViewModel implements GeofenceListener {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupsReference;
    private CollectionReference locationReference;
    private CollectionReference taskReference;
    private MutableLiveData<List<Group>> groupsForUser = new MutableLiveData<>();
    private MutableLiveData<List<Location>> locationsForUser = new MutableLiveData<>();
    private MutableLiveData<CODES> taskListener = new MutableLiveData<>();
    private Geofences geofences;
    private DocumentReference currentTaskDocument;

    public SaveTaskViewModel(@NonNull Application application) {
        super(application);

        groupsReference = db.collection("users")
                .document(user.getUid())
                .collection("groups");
        locationReference = db.collection("users")
                .document(user.getUid())
                .collection("locations");
        taskReference = db.collection("tasks");

        geofences = new Geofences(getApplication().getBaseContext(), this);
    }

    public LiveData<List<Group>> getGroupsForUser() {
        return groupsForUser;
    }

    public LiveData<List<Location>> getLocationsForUser() {
        return locationsForUser;
    }

    public LiveData<CODES> getTaskListener() {
        return taskListener;
    }

    public void addTask(Task task) {
        currentTaskDocument = taskReference.document();
        task.setUserId(user.getUid());
        task.setKey(currentTaskDocument.getId());

        if (task.isGeofenceActive()) {
            // Create the geofence, then create the task in firebase
            geofences.addGeofenceReminder(task, Utils.calculateDurationBetweenDates(task.getDueDate()));
        } else {
            addTaskToDatabase(task);
        }
    }

    private void addTaskToDatabase(Task task) {
        currentTaskDocument
                .set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        taskListener.postValue(CODES.TASK_ADDED);
                        Utils.updateGideonWidgets(getApplication().getBaseContext());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        taskListener.postValue(CODES.TASK_ERROR);
                    }
                });
    }

    private void addGeofenceForTask(Task task) {
        geofences.addGeofenceReminder(task, Utils.calculateDurationBetweenDates(task.getDueDate()));
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

    @Override
    public void onGeofenceListener(Geofences.CODES code, Task task) {
        switch (code) {
            case GEOFENCE_ADDED:
                addTaskToDatabase(task);
                break;
            case GEOFENCE_NOT_AVAILABLE:
                taskListener.postValue(CODES.GEOFENCE_NOT_AVAILABLE);
                break;
            case GEOFENCE_TOO_MANY_GEOFENCES:
                taskListener.postValue(CODES.GEOFENCE_TOO_MANY_GEOFENCES);
                break;
            case GEOFENCE_TOO_MANY_PENDING_INTENTS:
            case GEOFENCE_GENERIC_ERROR:
                taskListener.postValue(CODES.GEOFENCE_GENERIC_ERROR);
                break;
        }
    }

    public static enum CODES {
        TASK_ADDED,
        TASK_ERROR,
        GEOFENCE_NOT_AVAILABLE,
        GEOFENCE_TOO_MANY_GEOFENCES,
        GEOFENCE_GENERIC_ERROR
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        geofences = null;
    }
}
