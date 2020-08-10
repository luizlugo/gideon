package mx.volcanolabs.gideon.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Location implements Serializable {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String note;
    private boolean isDefaultLocation;
    private String key;

    public Location() {}

    public Location(String name, String address, Double latitude, Double longitude, String note, boolean isDefaultLocation) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.isDefaultLocation = isDefaultLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDefaultLocation() {
        return isDefaultLocation;
    }

    public void setDefaultLocation(boolean defaultLocation) {
        isDefaultLocation = defaultLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
