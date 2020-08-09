package mx.volcanolabs.gideon.models.mappers;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import mx.volcanolabs.gideon.models.Group;

public class GroupMapper {
    public static Group transform(QueryDocumentSnapshot document) {
        Group group = new Group();
        group.setName(document.getString("name"));
        group.setNote(document.getString("note"));
        group.setKey(document.getId());
        return group;
    }
}
