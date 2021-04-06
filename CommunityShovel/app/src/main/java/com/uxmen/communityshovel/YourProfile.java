package com.uxmen.communityshovel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class YourProfile extends AppCompatActivity implements View.OnClickListener {

    private static String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_your_profile);

        if (savedInstanceState != null) {
            String s = new String(savedInstanceState.getString(KEY));
            Log.d(DEBUG, s);
        }

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
    }

    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Log.d(DEBUG, "onSavedInstanceState()");
        savedInstance.putString(KEY, DATA);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            Toast.makeText(this, "Going Home", Toast.LENGTH_SHORT).show();
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            Toast.makeText(this, "Creating Request", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.profile_button) {
            Toast.makeText(this, "Viewing Profile", Toast.LENGTH_SHORT).show();
            switchActivity(YourProfile.class);
        }
    }

    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}