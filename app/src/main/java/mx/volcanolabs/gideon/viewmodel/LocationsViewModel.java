package mx.volcanolabs.gideon.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;

public class LocationsViewModel extends AndroidViewModel {
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference locationsDbReference = FirebaseDatabase.getInstance().getReference("Locations");
    private MutableLiveData<List<Location>> locationsObserver = new MutableLiveData<>();
    private LocationsEventListener locationsEventListener = new LocationsEventListener();

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public LocationsViewModel(@NonNull Application application) {
        super(application);
        locationsDbReference.child(currentUser.getUid()).addValueEventListener(locationsEventListener);
    }

    public LiveData<List<Location>> getLocationsObserver() {
        return locationsObserver;
    }

    public void removeLocation(Location location) {
        locationsDbReference.child(currentUser.getUid()).child(location.getKey()).removeValue();
    }

    private class LocationsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Location> locations = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Location location = snapshot.getValue(Location.class);
                location.setKey(snapshot.getKey());
                locations.add(location);
            }
            locationsObserver.postValue(locations);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        locationsDbReference.child(currentUser.getUid()).removeEventListener(locationsEventListener);
    }
}
