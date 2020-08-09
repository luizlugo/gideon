package mx.volcanolabs.gideon.models;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String key;
    private String description;
    private String dueDate;
    private String priority;
    private Group group;
    private Location location;
    private String userId;
    private boolean isGeofenceActive;
    private boolean completed;

    public Task() {
    }

    public Task(String key, String description, String dueDate, String priority, Group group, Location location, boolean isGeofenceActive) {
        this.key = key;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.group = group;
        this.location = location;
        this.isGeofenceActive = isGeofenceActive;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isGeofenceActive() {
        return isGeofenceActive;
    }

    public void setGeofenceActive(boolean geofenceActive) {
        isGeofenceActive = geofenceActive;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
