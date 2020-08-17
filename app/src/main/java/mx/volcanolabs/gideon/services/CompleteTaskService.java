package mx.volcanolabs.gideon.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import mx.volcanolabs.gideon.libs.Geofences;
import mx.volcanolabs.gideon.libs.NotificationsLibrary;
import mx.volcanolabs.gideon.models.Task;
import mx.volcanolabs.gideon.viewmodel.GeofenceListener;
import timber.log.Timber;

import static mx.volcanolabs.gideon.Constants.NOTIFICATION_ID;
import static mx.volcanolabs.gideon.Constants.TASK_ID;

public class CompleteTaskService extends JobIntentService implements GeofenceListener {
    private static final int COMPLETE_TASK_JOB_ID = 1213213213;
    private FirebaseFirestore db;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, CompleteTaskService.class, COMPLETE_TASK_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String taskId = intent.getStringExtra(TASK_ID);
        int notificationId = Integer.parseInt(intent.getStringExtra(NOTIFICATION_ID));
        db = FirebaseFirestore.getInstance();
        cancelNotification(notificationId);

        if (taskId != null) {
            db.collection("tasks")
                    .document(taskId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> result) {
                            if (result.getResult() != null) {
                                Task task = result.getResult().toObject(Task.class);
                                removeGeofence(task);
                            }
                        }
                    });
        }
    }

    private void removeGeofence(Task task) {
        Geofences geofences = new Geofences(this, this);
        geofences.removeGeofence(task);
    }

    private void cancelNotification(int notificationId) {
        NotificationsLibrary.cancelNotification(notificationId, this);
    }

    @Override
    public void onGeofenceListener(Geofences.CODES code, mx.volcanolabs.gideon.models.Task task) {
        updateTaskToComplete(task);
    }

    private void updateTaskToComplete(Task task) {
        db.collection("tasks")
                .document(task.getKey())
                .update("completed", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.getException() != null) {
                            Timber.e(task.getException());
                        }
                    }
                });
    }
}
