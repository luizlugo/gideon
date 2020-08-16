package mx.volcanolabs.gideon.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import mx.volcanolabs.gideon.libs.NotificationsLibrary;

import static mx.volcanolabs.gideon.Constants.NOTIFICATION_ID;
import static mx.volcanolabs.gideon.Constants.TASK_ID;

public class CompleteTaskService extends JobIntentService {
    private static final int COMPLETE_TASK_JOB_ID = 1213213213;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, CompleteTaskService.class, COMPLETE_TASK_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String taskId = intent.getStringExtra(TASK_ID);
        int notificationId = Integer.parseInt(intent.getStringExtra(NOTIFICATION_ID));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (taskId != null) {
            db.collection("tasks")
                    .document(taskId)
                    .update("completed", true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            cancelNotification(notificationId);
                        }
                    });
        }
    }

    private void cancelNotification(int notificationId) {
        NotificationsLibrary.cancelNotification(notificationId, this);
    }
}
