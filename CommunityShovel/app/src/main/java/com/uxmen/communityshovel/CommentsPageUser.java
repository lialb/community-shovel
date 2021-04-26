package com.uxmen.communityshovel;

import android.content.DialogInterface;
import android.content.Intent;


import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsPageUser  extends AppCompatActivity implements View.OnClickListener{

    private static String DEBUG = "DEBUG";

    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private User commentUser;
    private User activeUser;
    private com.uxmen.communityshovel.Request curRequest;
    private Integer len;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_comment_your_profile);

        activeUser = getIntent().getParcelableExtra("active_user");
        commentUser = getIntent().getParcelableExtra("selected_user");
        Log.d(DEBUG, "YourProfile: bio = " + activeUser.getBio());

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);



        this.ll = (LinearLayout) findViewById(R.id.comments_layout);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        String finalEmail = activeUser.getEmail().replace('.', ',');
        String url ="http://10.0.2.2:5000/get-user/" + finalEmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(DEBUG, response.toString());
                            JSONArray commentArr = response.getJSONArray("comments");
                            len = commentArr.length();
                            populateComments(commentArr);
                        } catch (JSONException e) {
                            Log.e("JSONObject Error", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void populateComments(JSONArray commentArr) {

            for (int i = 0; i < commentArr.length(); ++i) {
                LinearLayout templl = new LinearLayout(this);
                templl.setOrientation(LinearLayout.HORIZONTAL);
                // Comment:
                TextView commentView = new TextView(this);
                Button btnTag = new Button(this);
                try {
                    commentView.setText(commentArr.getJSONObject(i).getString("comment"));
                    commentView.setId(i);
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText(commentArr.getJSONObject(i).getString("name") + ": ");
                    String email = commentArr.getJSONObject(i).getString("user_id");
                    btnTag.setId(-i);
                    String url ="http://10.0.2.2:5000/get-user/" + email;
                    Log.d(DEBUG,"Testing email " + email);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d(DEBUG, response.toString());

                                        String firstName = response.getString("first_name");
                                        String lastName = response.getString("last_name");
                                        String bio = response.getString("bio");
                                        int distanceShoveled = response.getInt("distance_shoveled");
                                        int peopleImpacted = response.getInt("people_impacted");
                                        btnTag.setOnClickListener(getOnClickDoSomething(btnTag, email, firstName, lastName, bio, distanceShoveled, peopleImpacted));
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
                } catch (JSONException e) {
                    Log.e("JSONObject Error", e.getMessage());
                }


                // Name
//                TextView nameView = new TextView(this);
//                nameView.setText(commentArr.getJSONObject(i).getString("name") + ": ");
//                nameView.setTypeface(null, Typeface.BOLD);
//                nameView.setId(-i);

                // add view
                templl.addView(btnTag);
                templl.addView(commentView);

                // Draw box
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(0xFFFFFFFF);
                gd.setCornerRadius(5);
                gd.setStroke(1, 0xFF000000);
                templl.setBackground(gd);
                templl.setPadding(0, 10, 0, 0);
                ll.addView(templl);
            }

    }


    // to post comment to requests, copied from EditProfile


    View.OnClickListener getOnClickDoSomething(final Button button, final String email, final String firstName, final String lastName, final String bio, final Integer distanceShoveled, Integer peopleImpacted)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                commentUser = new User(email, firstName, lastName, bio, distanceShoveled, peopleImpacted);
                viewUserProfile();
            }
        };
    }




    public void viewUserProfile() {
        Intent intent = new Intent(getBaseContext(), Profile.class);
        intent.putExtra("selected_user", commentUser);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
    }


    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            Toast.makeText(this, "Going Home", Toast.LENGTH_SHORT).show();
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            Toast.makeText(this, "Creating Request", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        }
    }



    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        intent.putExtra("selected_user", commentUser);
        startActivity(intent);
    }
}
