package mx.volcanolabs.gideon.libs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.UUID;

import mx.volcanolabs.gideon.MainActivity;
import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.TaskBroadcastReceiver;
import mx.volcanolabs.gideon.models.Task;

import static mx.volcanolabs.gideon.Constants.COMPLETE_TASK;
import static mx.volcanolabs.gideon.Constants.DEFAULT_DATE_KEY;
import static mx.volcanolabs.gideon.Constants.NOTIFICATION_ID;
import static mx.volcanolabs.gideon.Constants.TASK_ID;

public class NotificationsLibrary {
    private static final String TASK_NOTIFICATION_CHANNEL_ID = "190329";

    public static void displayNotificationForTask(Task task, Context context) {
        int notificationId = (int) -new Date().getTime();
        NotificationCompat.Builder notificationBuilder = createNotificationBuilder(notificationId, task, context);
        createNotificationChannel(context);
        displayNotification(notificationBuilder, notificationId, context);
    }

    public static void cancelNotification(int notificationId, Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

    private static void displayNotification(NotificationCompat.Builder notificationBuilder, int notificationId, Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private static NotificationCompat.Builder createNotificationBuilder(int notificationId, Task task, Context context) {
        return new NotificationCompat.Builder(context, TASK_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.task_near_you))
                .setContentText(task.getLocation().getName())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(task.getDescription()))
                .setContentIntent(getPendingIntent(task, context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_done, context.getString(R.string.complete), getCompletePendingIntent(notificationId, task.getKey(), context))
                .setAutoCancel(true);
    }

    private static PendingIntent getCompletePendingIntent(int notificationId, String taskId, Context context) {
        Intent completeTaskActionIntent = new Intent(context, TaskBroadcastReceiver.class);
        completeTaskActionIntent.setAction(COMPLETE_TASK);
        completeTaskActionIntent.putExtra(TASK_ID, taskId);
        completeTaskActionIntent.putExtra(NOTIFICATION_ID, String.valueOf(notificationId));
        return PendingIntent.getBroadcast(context, notificationId, completeTaskActionIntent, 0);
    }

    private static PendingIntent getPendingIntent(Task task, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(DEFAULT_DATE_KEY, task.getDueDate());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.tasks_channel_name);
            String description = context.getString(R.string.tasks_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(TASK_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
