package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mx.volcanolabs.gideon.models.Location;

public class SaveLocationViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference locationsDbReference = FirebaseDatabase.getInstance().getReference("Locations");
    private MutableLiveData<Boolean> locationsObserver = new MutableLiveData<>();

    public SaveLocationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLocationsObserver() {
        return locationsObserver;
    }

    public void addLocation(Location location) {
        locationsDbReference.child(user.getUid()).push().setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                locationsObserver.postValue(true);
            }
        });
    }

    public void updateLocation(Location location) {
        locationsDbReference.child(user.getUid()).child(location.getKey()).setValue(location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                locationsObserver.postValue(true);
            }
        });
    }
}
