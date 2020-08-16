package mx.volcanolabs.gideon.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import mx.volcanolabs.gideon.libs.NotificationsLibrary;
import mx.volcanolabs.gideon.models.Task;

import static mx.volcanolabs.gideon.Constants.TASK_ID;

public class GeofenceService extends JobIntentService {
    private static final int GEOFENCE_JOB_ID = 12345;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceService.class, GEOFENCE_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String taskId = intent.getStringExtra(TASK_ID);
        getTaskInfoAndNotify(taskId);
    }

    private void getTaskInfoAndNotify(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> results) {
                        if (results.getResult() != null) {
                            Task task = results.getResult().toObject(Task.class);

                            if (task != null) {
                                task.setKey(taskId);
                                createLocalNotification(task);
                            }
                        }
                    }
                });
    }

    private void createLocalNotification(Task task) {
        NotificationsLibrary.displayNotificationForTask(task, this);
    }
}
