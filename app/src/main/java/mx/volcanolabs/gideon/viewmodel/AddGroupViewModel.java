package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mx.volcanolabs.gideon.models.Group;

public class AddGroupViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference groupsDbReference = FirebaseDatabase.getInstance().getReference("Groups");
    public MutableLiveData<Boolean> groupsListener = new MutableLiveData<>();

    public AddGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public void saveGroup(String name, String note) {
        Group group = new Group(name, note);
        groupsDbReference.child(user.getUid()).push().setValue(group).addOnSuccessListener(v -> groupsListener.postValue(true));
    }
}
