package mx.volcanolabs.gideon.models.mappers;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import mx.volcanolabs.gideon.models.Location;

public class LocationMapper {
    public static Location transform(QueryDocumentSnapshot document) {
        Location location = new Location();
        location.setLatitude(document.getDouble("latitude"));
        location.setLongitude(document.getDouble("longitude"));
        location.setAddress(document.getString("address"));
        location.setDefaultLocation(document.getBoolean("defaultLocation"));
        location.setKey(document.getId());
        location.setName(document.getString("name"));
        location.setNote(document.getString("note"));
        return location;
    }
}
