package mx.volcanolabs.gideon.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import mx.volcanolabs.gideon.MainActivity;
import mx.volcanolabs.gideon.R;
import mx.volcanolabs.gideon.models.Task;

import static mx.volcanolabs.gideon.Constants.due_date_format;

public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Task> tasks;
    private CollectionReference taskReference;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(due_date_format, Locale.US);

    public GridRemoteViewsFactory(Context context) {
        mContext = context;
        db = FirebaseFirestore.getInstance();
        taskReference = db.collection("tasks");
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Date currentDate = new Date();
            CountDownLatch doneSignal = new CountDownLatch(1);

            if (user != null) {
                taskReference
                        .whereEqualTo("dueDate", dateFormat.format(currentDate))
                        .whereEqualTo("completed", false)
                        .orderBy("priority")
                        .get()
                        .addOnCompleteListener(queryTask -> {
                            if (queryTask.getResult() != null) {
                                tasks = new ArrayList<>();
                                for (QueryDocumentSnapshot document : queryTask.getResult()) {
                                    Task task = document.toObject(Task.class);
                                    tasks.add(task);
                                }
                                doneSignal.countDown();
                            }
                        });

                doneSignal.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (tasks == null) return 0;
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Task task = tasks.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.gideon_widget_task_list_item);
        views.setTextViewText(R.id.tv_description, task.getDescription());
        views.setTextViewText(R.id.tv_location, task.getLocation().getName());
        views.setTextViewText(R.id.tv_group, task.getGroup().getName());
        views.setOnClickFillInIntent(R.id.vg_container, new Intent());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
