package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;

public class MainViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference tasksDbReference = FirebaseDatabase.getInstance().getReference("Tasks").child(user.getUid());
    private MutableLiveData<List<Task>> taskListener = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Task>> getTaskListener() {
        return taskListener;
    }

    public void updateTask(Task task) {
        tasksDbReference.child(task.getKey()).setValue(task);
    }

    public void filterTasks(String dueDate, boolean completed) {
        tasksDbReference.orderByChild("dueDate").equalTo(dueDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> tasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    task.setKey(snapshot.getKey());

                    if (task.isCompleted() == completed) {
                        tasks.add(task);
                    }
                }
                taskListener.postValue(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
