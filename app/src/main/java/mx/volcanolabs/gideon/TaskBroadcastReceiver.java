package mx.volcanolabs.gideon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mx.volcanolabs.gideon.services.CompleteTaskService;

import static mx.volcanolabs.gideon.Constants.COMPLETE_TASK;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(COMPLETE_TASK)) {
            CompleteTaskService.enqueueWork(context, intent);
        }
    }
}
