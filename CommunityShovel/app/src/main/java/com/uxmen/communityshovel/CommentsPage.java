package com.uxmen.communityshovel;

import android.content.Intent;


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
    private TextView comment1;
    private TextView comment2;
    private TextView comment3;
    private TextView commentCur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        activeUser = getIntent().getParcelableExtra("active_user");
        curRequest = getIntent().getParcelableExtra("cur_request");

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        this.postComment = (Button) findViewById(R.id.add_comment_button);
        this.comment1 = (TextView) findViewById(R.id.Comment1);
        this.comment2 = (TextView) findViewById(R.id.Comment2);
        this.comment3 = (TextView) findViewById(R.id.Comment3);
        this.commentCur = (TextView) findViewById(R.id.CommentCurrent);


        this.comment1.setVisibility(View.GONE);
        this.comment2.setVisibility(View.GONE);
        this.comment3.setVisibility(View.GONE);
        this.commentCur.setVisibility(View.GONE);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        postComment.setOnClickListener(this);

        addComment = (EditText) findViewById(R.id.comment_text);

        String commentCheck = curRequest.getComments();
        try {
            JSONArray commentArr = new JSONArray(commentCheck);

            if (commentArr.length() >= 1) {
                this.comment1.setVisibility(View.VISIBLE);
                try {
                    this.comment1.setText(commentArr.getJSONObject(0).getString("comment"));
                } catch (JSONException e) {
                    Log.e("JSONObject Error", e.getMessage());
                }
            }

            if (commentArr.length() >= 2) {
                this.comment2.setVisibility(View.VISIBLE);
                try {
                    this.comment2.setText(commentArr.getJSONObject(1).getString("comment"));
                } catch (JSONException e) {
                    Log.e("JSONObject Error", e.getMessage());
                }
            }

            if (commentArr.length() >= 3) {
                this.comment3.setVisibility(View.VISIBLE);
                try {
                    this.comment3.setText(commentArr.getJSONObject(2).getString("comment"));
                } catch (JSONException e) {
                    Log.e("JSONObject Error", e.getMessage());
                }
            }
        }  catch (JSONException e) {
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

        findViewById(R.id.CommentCurrent).setVisibility(View.VISIBLE);
        Log.d(DEBUG, this.addComment.getText().toString());
        this.commentCur.setText(this.addComment.getText().toString());
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
