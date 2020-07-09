package mx.volcanolabs.gideon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import mx.volcanolabs.gideon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        setupActionBar();
    }

    private void setupActionBar() {
        setSupportActionBar(view.toolbar);
    }
}