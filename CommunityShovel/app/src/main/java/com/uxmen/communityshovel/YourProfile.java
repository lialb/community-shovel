package com.uxmen.communityshovel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class YourProfile extends AppCompatActivity implements View.OnClickListener {

    private static String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private ImageButton editButton;
    private Button viewCommentsButton;
    private User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_your_profile);

        if (savedInstanceState != null) {
            String s = new String(savedInstanceState.getString(KEY));
            Log.d(DEBUG, s);
        }

        activeUser = getIntent().getParcelableExtra("active_user");
        Log.d(DEBUG, "YourProfile: bio = " + activeUser.getBio());

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        editButton = (ImageButton) findViewById(R.id.edit_profile_button);
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        viewCommentsButton = (Button) findViewById(R.id.your_profile_view_comments_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        viewCommentsButton.setOnClickListener(this);

        fillUserInfo();
    }

    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Log.d(DEBUG, "onSavedInstanceState()");
        savedInstance.putString(KEY, DATA);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            switchActivity(CreateRequest.class);
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.edit_profile_button) {
            switchActivity(EditYourProfile.class);
        } else if (v.getId() == R.id.logout_button) {
            // switch back to sign in screen without passing active user
            Intent intent = new Intent(this, SignInPage.class);
            startActivity(intent);
        } else if (v.getId() == R.id.your_profile_view_comments_button) {
            Toast.makeText(this, "View Profile Comments", Toast.LENGTH_SHORT).show();
            // TODO: switch to Hritik's View Comments activity
            switchActivity(CommentsPageUser.class);
        }
    }

    /**
     * fillUserInfo populates the textViews on the Profile page with the user's data
     */
    public void fillUserInfo() {
        TextView textViewName = (TextView) findViewById(R.id.name_text);
        TextView textViewEmail = (TextView) findViewById(R.id.email_text);
        TextView textViewDistanceShoveled = (TextView) findViewById(R.id.distance_shoveled_text);
        TextView textViewPeopleImpacted = (TextView) findViewById(R.id.people_impacted_text);
        TextView textViewBio = (TextView) findViewById(R.id.bio_text);

        textViewName.setText(activeUser.getFirstName() + " " + activeUser.getLastName());
        textViewEmail.setText(activeUser.getEmail().replace(',','.'));
        textViewDistanceShoveled.setText(activeUser.getDistanceShoveled()+"");
        textViewPeopleImpacted.setText(activeUser.getPeopleImpacted()+"");
        textViewBio.setText(activeUser.getBio());
    }


    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }
}