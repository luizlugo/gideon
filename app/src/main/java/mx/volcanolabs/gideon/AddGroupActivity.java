package mx.volcanolabs.gideon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import mx.volcanolabs.gideon.databinding.ActivityAddGroupBinding;

public class AddGroupActivity extends AppCompatActivity {
    ActivityAddGroupBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = ActivityAddGroupBinding.inflate(getLayoutInflater());
        view.btnBack.setOnClickListener(v -> finish());
        setContentView(view.getRoot());
    }
}