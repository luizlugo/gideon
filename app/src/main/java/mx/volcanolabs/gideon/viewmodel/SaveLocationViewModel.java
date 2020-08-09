package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import mx.volcanolabs.gideon.models.Location;

public class SaveLocationViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> locationsObserver = new MutableLiveData<>();

    public SaveLocationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLocationsObserver() {
        return locationsObserver;
    }

    public void addLocation(Location location) {
        db.collection("users")
                .document(user.getUid())
                .collection("locations")
                .add(location)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        locationsObserver.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        locationsObserver.postValue(false);
                    }
                });
    }

    public void updateLocation(Location location) {
        db.collection("users")
                .document(user.getUid())
                .collection("locations")
                .document(location.getKey())
                .set(location)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        locationsObserver.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        locationsObserver.postValue(false);
                    }
                });
    }
}
