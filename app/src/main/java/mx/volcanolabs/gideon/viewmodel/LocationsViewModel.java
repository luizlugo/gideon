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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import mx.volcanolabs.gideon.models.Location;

public class LocationsViewModel extends AndroidViewModel {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Location>> locationsObserver = new MutableLiveData<>();
    private CollectionReference locationsCollection;
    private ListenerRegistration locationsListenerRegistration;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public LocationsViewModel(@NonNull Application application) {
        super(application);
        locationsCollection = db
                .collection("users")
                .document(user.getUid())
                .collection("locations");
    }

    public LiveData<List<Location>> getLocationsObserver() {
        return locationsObserver;
    }

    public void removeLocation(Location location) {
        locationsCollection
                .document(location.getKey())
                .delete();
    }

    public void getLocations() {
        locationsListenerRegistration = locationsCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // TODO: throw error
                            return;
                        }

                        List<Location> locations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            locations.add(document.toObject(Location.class));
                        }
                        locationsObserver.postValue(locations);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        locationsCollection = null;
        locationsListenerRegistration.remove();
    }
}
