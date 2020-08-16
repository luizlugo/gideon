package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Task;

import static mx.volcanolabs.gideon.Constants.GIDEON_LOCATION_PERMISSION_KEY;
import static mx.volcanolabs.gideon.Constants.GIDEON_SHARED_PREFERENCES;

public class MainViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference taskReference;
    private MutableLiveData<List<Task>> taskListener = new MutableLiveData<>();
    private MutableLiveData<Boolean> tasksSourceChanged = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        taskReference = db.collection("tasks");
    }

    public LiveData<List<Task>> getTaskListener() {
        return taskListener;
    }

    public LiveData<Boolean> getTaskSourceChanged() {
        return tasksSourceChanged;
    }

    public void updateTask(Task task) {
        taskReference
                .document(task.getKey())
                .update("completed", task.isCompleted())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            tasksSourceChanged.setValue(true);
                        }
                    }
                });
    }

    public void filterTasks(String dueDate, boolean completed) {
        taskReference
                .whereEqualTo("dueDate", dueDate)
                .whereEqualTo("completed", completed)
                .orderBy("priority")
                .get()
                .addOnCompleteListener(queryTask -> {
                    List<Task> tasks = new ArrayList<>();

                    if (queryTask.getResult() != null) {
                        for (QueryDocumentSnapshot document : queryTask.getResult()) {
                            Task task = document.toObject(Task.class);
                            task.setKey(document.getId());
                            tasks.add(task);
                        }
                    }

                    taskListener.postValue(tasks);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
