package mx.volcanolabs.gideon.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Group implements Serializable {
    private String key;
    private String name;
    private String note;

    public Group() {}

    public Group(String name, String note) {
        this.name = name;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
