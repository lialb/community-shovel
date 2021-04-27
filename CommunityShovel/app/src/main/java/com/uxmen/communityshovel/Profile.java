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

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private Button viewCommentsButton;
    private User selectedUser;
    private User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_profile);

        if (savedInstanceState != null) {
            String s = new String(savedInstanceState.getString(KEY));
            Log.d(DEBUG, s);
        }

        activeUser = getIntent().getParcelableExtra("active_user");
        selectedUser = getIntent().getParcelableExtra("selected_user");
        Log.d(DEBUG, "YourProfile: bio = " + selectedUser.getBio());

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        viewCommentsButton = (Button) findViewById(R.id.profile_view_comments_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
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
        } else if (v.getId() == R.id.profile_view_comments_button) {
            Toast.makeText(this, "View Profile Comments", Toast.LENGTH_SHORT).show();
            // TODO: switch to Hritik's View Comments activity
            switchActivity(CommentsPageOther.class);
        }
    }

    /**
     * fillUserInfo populates the textViews on the Profile page with the user's data
     */
    public void fillUserInfo() {
        TextView textViewName = (TextView) findViewById(R.id.profile_name_text);
        TextView textViewEmail = (TextView) findViewById(R.id.profile_email_text);
        TextView textViewDistanceShoveled = (TextView) findViewById(R.id.profile_distance_shoveled_text);
        TextView textViewPeopleImpacted = (TextView) findViewById(R.id.profile_people_impacted_text);
        TextView textViewBio = (TextView) findViewById(R.id.profile_bio_text);

        textViewName.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());
        textViewEmail.setText(selectedUser.getEmail().replace(',','.'));
        textViewDistanceShoveled.setText(selectedUser.getDistanceShoveled()+"");
        textViewPeopleImpacted.setText(selectedUser.getPeopleImpacted()+"");
        textViewBio.setText(selectedUser.getBio());
    }


    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        intent.putExtra("selected_user", selectedUser);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }
}