package com.uxmen.communityshovel;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentsPage  extends AppCompatActivity implements View.OnClickListener{

    private static String DEBUG = "DEBUG";

    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private User activeUser;
    private com.uxmen.communityshovel.Request curRequest;
    private Button postComment;
    private EditText addComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        // get data for body
        activeUser = getIntent().getParcelableExtra("active_user");
        curRequest = getIntent().getParcelableExtra("cur_request");

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        this.postComment = (Button) findViewById(R.id.add_comment_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        postComment.setOnClickListener(this);

        addComment = (EditText) findViewById(R.id.comment_text);

        LinearLayout ll = (LinearLayout) findViewById(R.id.comments_layout);
        String commentCheck = curRequest.getComments();
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
                TextView nameView = new TextView(this);
                nameView.setText(commentArr.getJSONObject(i).getString("name") + ": ");
                nameView.setTypeface(null, Typeface.BOLD);
                nameView.setId(-i);

                // add view
                templl.addView(nameView);
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
    }

    // to post comment to requests, copied from EditProfile
    private void postComment(View v) {

        String url ="http://10.0.2.2:5000/add-request-comment/" + curRequest.getRequestId().toString();
        JSONObject request = new JSONObject();
        try{
            request.put("comment", addComment.getText().toString());
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

//        findViewById(R.id.CommentCurrent).setVisibility(View.VISIBLE);
        Log.d(DEBUG, this.addComment.getText().toString());
//        this.commentCur.setText(this.addComment.getText().toString());
        this.addComment.setText("");

    }



    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            Toast.makeText(this, "Going Home", Toast.LENGTH_SHORT).show();
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            Toast.makeText(this, "Creating Request", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.add_comment_button) {
            postComment(v);
        }
    }
    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
    }
}
