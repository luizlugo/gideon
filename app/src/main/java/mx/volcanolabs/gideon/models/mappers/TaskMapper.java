package mx.volcanolabs.gideon.models.mappers;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import mx.volcanolabs.gideon.models.Group;
import mx.volcanolabs.gideon.models.Location;
import mx.volcanolabs.gideon.models.Task;

public class TaskMapper {
    public static Task transform(QueryDocumentSnapshot document) {
        try {
            Task task = new Task();
            task.setCompleted(document.getBoolean("completed"));
            task.setDescription(document.getString("description"));
            task.setDueDate(document.getString("dueDate"));
            task.setGeofenceActive(document.getBoolean("geofenceActive"));
            task.setGroup(document.get("group", Group.class));
            task.setLocation(document.get("location", Location.class));
            task.setPriority(document.getString("priority"));
            task.setUserId(document.getString("userId"));
            return task;
        } catch (Exception e) {
            throw e;
        }
    }
}
