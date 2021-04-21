package com.uxmen.communityshovel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditYourProfile extends AppCompatActivity implements View.OnClickListener {
    private static String DEBUG = "DEBUG";

    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private User activeUser;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_edit_your_profile);

        activeUser = getIntent().getParcelableExtra("active_user");
        Log.d(DEBUG, "YourProfile: bio = " + activeUser.getBio());

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        Button saveButton = (Button) findViewById(R.id.save_profile_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.first_name_edit_text);
        editTextLastName = (EditText) findViewById(R.id.last_name_edit_text);

        editTextBio = (EditText) findViewById(R.id.bio_edit_text);

        fillUserEditInfo();
    }

    private void fillUserEditInfo() {
        editTextFirstName.setText(activeUser.getFirstName());
        editTextLastName.setText(activeUser.getLastName());
        editTextBio.setText(activeUser.getBio());
    }

    private void saveProfileChanges(View v) {
        // NOTE: not allowed to change email/password
        // also not allowed to change volunteering statistics
        String new_firstName = editTextFirstName.getText().toString();
        String new_lastName = editTextLastName.getText().toString();
        String new_bio = editTextBio.getText().toString();

        String finalEmail = activeUser.getEmail().replace('.', ',');
        String url ="http://10.0.2.2:5000/update-user/" + finalEmail;

        JSONObject request = new JSONObject();
        try{
            request.put("first_name", new_firstName);
            request.put("last_name", new_lastName);
            request.put("bio", new_bio);
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
        }

        Log.d(DEBUG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, request, response -> {
                    Log.d(DEBUG, response.toString());
                    lookupUserAndGo(activeUser.getEmail());
                }, error -> {
                    if(error.networkResponse != null) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                    Toast.makeText(v.getContext(), "Invalid Login", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    // copied from sign in
    private void lookupUserAndGo(String email) {
        String finalEmail = email.replace('.', ',');
        String url ="http://10.0.2.2:5000/get-user/" + finalEmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(DEBUG, response.toString());

                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String bio = response.getString("bio");
                            int distanceShoveled = response.getInt("distance_shoveled");
                            int peopleImpacted = response.getInt("people_impacted");
                            User u = new User(finalEmail, firstName, lastName, bio,
                                    distanceShoveled, peopleImpacted);
                            Intent intent = new Intent(getBaseContext(), YourProfile.class);
                            intent.putExtra("active_user", u);
                            startActivity(intent);

                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            switchActivity(CreateRequest.class);
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.save_profile_button) {
            saveProfileChanges(v);
            switchActivity(YourProfile.class);
        }
    }

    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
    }
}
