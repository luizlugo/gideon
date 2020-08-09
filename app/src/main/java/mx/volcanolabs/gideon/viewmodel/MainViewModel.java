package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Task;

public class MainViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference taskReference;
    private MutableLiveData<List<Task>> taskListener = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        taskReference = db.collection("tasks");
    }

    public LiveData<List<Task>> getTaskListener() {
        return taskListener;
    }

    public void updateTask(Task task) {
        taskReference.document(task.getKey()).set(task);
    }

    public void filterTasks(String dueDate, boolean completed) {
        taskReference
                .whereEqualTo("dueDate", dueDate)
                .whereEqualTo("completed", completed)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // TODO: Throw exception
                            return;
                        }

                        List<Task> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Task task = (Task) document.getData();
                            task.setKey(document.getId());

                            if (task.isCompleted() == completed) {
                                tasks.add(task);
                            }
                        }
                        taskListener.postValue(tasks);
                    }
                });
    }
}
