package com.uxmen.communityshovel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class CommentsPage  extends AppCompatActivity implements View.OnClickListener{

    private static String DEBUG = "DEBUG";

    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private User commentUser;
    private User activeUser;
    private com.uxmen.communityshovel.Request curRequest;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        // get data for body
        activeUser = getIntent().getParcelableExtra("active_user");
        curRequest = getIntent().getParcelableExtra("cur_request");
        commentUser = getIntent().getParcelableExtra("selected_user");

//        if (commentUser != null) {
//            activeUser = commentUser;
//        }

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        String commentCheck = curRequest.getComments();
//        refresh(commentCheck);

        this.ll = (LinearLayout) findViewById(R.id.comments_layout);

        try {
            JSONArray commentArr = new JSONArray(commentCheck);
            System.out.println(commentArr);
            for (int i = 0; i < commentArr.length(); ++i) {
                LinearLayout templl = new LinearLayout(this);
                templl.setOrientation(LinearLayout.HORIZONTAL);
                // Comment:
                TextView commentView = new TextView(this);
                commentView.setText(commentArr.getJSONObject(i).getString("comment"));
                commentView.setId(i);
                // Name
//                TextView nameView = new TextView(this);
//                nameView.setText(commentArr.getJSONObject(i).getString("name") + ": ");
//                nameView.setTypeface(null, Typeface.BOLD);
//                nameView.setId(-i);



                String email = commentArr.getJSONObject(i).getString("user_id");

                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setText(commentArr.getJSONObject(i).getString("name") + ": ");
                btnTag.setId(-i);


                String url ="http://10.0.2.2:5000/get-user/" + email;
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



                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

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
        } catch (JSONException e) {
            Log.e("JSONObject Error", e.getMessage());
        }

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);

            }
        });
    }

    View.OnClickListener getOnClickDoSomething(final Button button, final String email, final String firstName, final String lastName, final String bio, final Integer distanceShoveled, Integer peopleImpacted)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                commentUser = new User(email, firstName, lastName, bio, distanceShoveled, peopleImpacted);
                viewCommentProfile();
            }
        };
    }




    // to post comment to requests, copied from EditProfile
    private void postComment(View v, String s) {

            String url ="http://10.0.2.2:5000/add-request-comment/" + curRequest.getRequestId().toString();
            JSONObject request = new JSONObject();
            try{
                request.put("comment", s);
                request.put("name", activeUser.getFirstName() + " " + activeUser.getLastName());
                request.put("user_id", activeUser.getEmail());
            }catch(JSONException e){
                Log.e("JSONObject Error", e.getMessage());
            }

            Log.d(DEBUG, request.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, request, response -> {
                        Log.d(DEBUG, response.toString());
                    }, error -> {
                        if(error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                        Toast.makeText(v.getContext(), "Invalid Comment", Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        // refresh here

       addView(activeUser.getFirstName() + " " + activeUser.getLastName(), s);

    }

    private void addView(String nameData, String commentData) {
            String commentChecknew = curRequest.getComments();

            LinearLayout templlnew = new LinearLayout(this);
            templlnew.setOrientation(LinearLayout.HORIZONTAL);
            TextView latestComment = new TextView(this);
            latestComment.setText(commentData);
            latestComment.setId(commentChecknew.length());


            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(nameData + ": ");
            btnTag.setId(-(commentChecknew.length()));


            templlnew.addView(btnTag);
            templlnew.addView(latestComment);

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(0xFFFFFFFF);
            gd.setCornerRadius(5);
            gd.setStroke(1, 0xFF000000);
            templlnew.setBackground(gd);
            templlnew.setPadding(0, 10, 0, 10);
            this.ll.addView(templlnew);
    }

    public void showAlertDialogButtonClicked(View view) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a comment");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.activity_comment_modal, null);
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("Post Comment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                EditText editText = customLayout.findViewById(R.id.editTextModal);
                sendDialogDataToActivity(editText.getText().toString(), view);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    // do something with the data coming from the AlertDialog
    private void sendDialogDataToActivity(String data, View v) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        postComment(v, data);
    }



    public void viewCommentProfile() {
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
