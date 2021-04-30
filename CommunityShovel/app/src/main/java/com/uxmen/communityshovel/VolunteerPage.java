package com.uxmen.communityshovel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class VolunteerPage extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";

    private User activeUser;
    private User selectedUser;
    private Request curRequest;
    private SupportMapFragment mapFragment;
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private Button viewVolunteerProfileButton;
    private Button stopVolunteeringButton;
    private Button markPartialButton;
    private Button markCompleteButton;
    private ImageButton upvoteButton;

    private GoogleMap map;

    private Marker curMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_page);

        activeUser = getIntent().getParcelableExtra("active_user");
        curRequest = getIntent().getParcelableExtra("cur_request");


        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        viewVolunteerProfileButton = (Button) findViewById(R.id.volunteer_view_volunteer_button);
        upvoteButton = (ImageButton) findViewById(R.id.volunteer_upvote_button);
        stopVolunteeringButton = (Button) findViewById(R.id.stop_volunteer_button);
        markPartialButton = (Button) findViewById(R.id.mark_partial_button);
        markCompleteButton = (Button) findViewById(R.id.mark_complete_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        //viewVolunteerProfileButton.setOnClickListener(this);
        upvoteButton.setOnClickListener(this);
        stopVolunteeringButton.setOnClickListener(this);
        markPartialButton.setOnClickListener(this);
        markCompleteButton.setOnClickListener(this);

        TextView textViewVolunteerLocation = (TextView) findViewById(R.id.volunteer_location_text);
        TextView textViewVolunteerUpvotes = (TextView) findViewById(R.id.volunteer_upvotes_text);
        TextView textViewVolunteerInfo = (TextView) findViewById(R.id.volunteer_info_text);
        TextView textViewVolunteerStatus = (TextView) findViewById(R.id.volunteer_status_text);

        Context context = this;
        Geocoder geo = new Geocoder(context);
        String selectionLocation = "";
        try {
            List<Address> requestAddress = geo.getFromLocation(this.curRequest.getXCoord(),
                    this.curRequest.getYCoord(), 1);
            selectionLocation = requestAddress.get(0).getAddressLine(0).split(",", 2)[0];
        } catch (IOException e) {
            Log.d(DEBUG, "Could not find address");
        }

        String statusText = "Incomplete";
        textViewVolunteerStatus.setTextColor(this.getResources().getColor(R.color.incomplete));
        if (curRequest.getStatus() == 1) {
            statusText = "Partially complete";
            textViewVolunteerStatus.setTextColor(this.getResources().getColor(R.color.partially_complete));
        } else if (curRequest.getStatus() == 2) {
            statusText = "Complete";
            textViewVolunteerStatus.setTextColor(this.getResources().getColor(R.color.complete));
        }


        textViewVolunteerLocation.setText(selectionLocation);
        textViewVolunteerUpvotes.setText(String.valueOf(curRequest.getUpvotes()));
        textViewVolunteerInfo.setText(curRequest.getInfo());
        textViewVolunteerStatus.setText(statusText);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.addVolunteerToRequest();

        //this.getVolunteer();
        viewVolunteerProfileButton.setText(activeUser.getFirstName());
    }

    public void onMapReady(GoogleMap map) {

        // for each request:
        this.map = map;
        double xCoord = this.curRequest.getXCoord();
        double yCoord = this.curRequest.getYCoord();

        final LatLng loc = new LatLng(xCoord, yCoord);
        // add marker for request
        this.curMarker = map.addMarker(new MarkerOptions()
                .position(loc)
                .title("Request"));
        curMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mapmarker_highlighted",114,152)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
        // Set a listener for marker click.
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    protected void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Log.d(DEBUG, "onSavedInstanceState()");
        savedInstance.putString(KEY, DATA);
    }

    protected void onStart() {
        super.onStart();
        Log.d(DEBUG, "OnStart()");
    }

    protected void onResume() {
        super.onResume();
        Log.d(DEBUG, "OnResume()");
    }

    protected void onRestart() {
        super.onRestart();
        Log.d(DEBUG, "OnRestart()");
    }

    protected void onPause() {
        super.onPause();
        Log.d(DEBUG, "OnPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.d(DEBUG, "OnStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG, "OnDestroy()");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.home_button) {
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.create_request_button) {
            switchActivity(CreateRequest.class);
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.volunteer_view_volunteer_button) {
            Log.d(DEBUG, "Viewing volunteer profile");
            viewVolunteerProfile();
        } else if (v.getId() == R.id.volunteer_upvote_button) {
            Log.d(DEBUG, "Upvoting selection");
            upvoteSelection();
            TextView upvotesView = (TextView) findViewById(R.id.volunteer_upvotes_text);
            upvotesView.setText(String.valueOf(Integer.parseInt((String)upvotesView.getText()) + 1));
            this.curRequest.setUpvotes(curRequest.getUpvotes() + 1);
        }

        else if (v.getId() == R.id.stop_volunteer_button) {
            stopVolunteer();
            Log.d(DEBUG, "Stopping volunteer");
        } else if (v.getId() == R.id.mark_partial_button) {
            partialVolunteer();
            Log.d(DEBUG, "Partially completed volunteering");
        } else if (v.getId() == R.id.mark_complete_button) {
            completedVolunteer();
            Log.d(DEBUG, "Completed volunteering");
        }
    }

    public void getVolunteer() {
        try {
            JSONObject jsonObject = new JSONObject(this.curRequest.getVolunteers());
            Iterator<String> keys = jsonObject.keys();
            String volunteerId = keys.next();
            Log.d(DEBUG, "volunteerId: " + volunteerId);
            String url ="http://10.0.2.2:5000/get-user/" + volunteerId;
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
                                selectedUser = new User(volunteerId, firstName, lastName, bio,
                                        distanceShoveled, peopleImpacted);
                                viewVolunteerProfileButton.setText(selectedUser.getFirstName());
                                Log.d(DEBUG, "selectedUser bio: " + selectedUser.getBio());
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

        } catch(JSONException e) {
            Log.e("JSON Exception", e.getMessage());
        }
    }

    public void viewVolunteerProfile() {
        Intent intent = new Intent(getBaseContext(), Profile.class);
        intent.putExtra("selected_user", selectedUser);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
    }

    public void upvoteSelection() {
        String url;
        // find request based on the provided key
        try {
            url = "http://10.0.2.2:5000/upvote-request/" + this.curRequest.getRequestId();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void stopVolunteer() {
        User u = this.activeUser;

        new AlertDialog.Builder(this)
                .setTitle("Please Confirm")
                .setMessage("Do you really want to stop volunteering?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeVolunteerFromRequest();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("active_user", u);
                        startActivity(intent);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void addVolunteerToRequest() {
        String url;
        // find request based on the provided key
        try {
            url = "http://10.0.2.2:5000/volunteer-for-request/" + this.curRequest.getRequestId();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return;
        }

        JSONObject request = new JSONObject();
        try{
            request.put("email", this.activeUser.getEmail());
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.PUT, url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void removeVolunteerFromRequest() {
        String url;
        // find request based on the provided key
        try {
            url = "http://10.0.2.2:5000/remove-volunteer/" + this.curRequest.getRequestId();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return;
        }

        JSONObject request = new JSONObject();
        try{
            request.put("email", this.activeUser.getEmail());
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.PUT, url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void partialVolunteer() {
        User u = this.activeUser;
        new AlertDialog.Builder(this)
                .setTitle("Please Confirm")
                .setMessage("Do you really want to stop volunteering and mark partially complete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        updateReqStatus(1);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("active_user", u);
                        startActivity(intent);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void completedVolunteer() {
        User u = this.activeUser;
        new AlertDialog.Builder(this)
                .setTitle("Please Confirm")
                .setMessage("Is the volunteer job completed?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        updateReqStatus(2);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("active_user", u);
                        startActivity(intent);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    //Update the status of curMarker
    public void updateReqStatus(int status) {
        String url;
        // find request based on the provided key
        try {
            url = "http://10.0.2.2:5000/update-request/" + this.curRequest.getRequestId();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.PUT, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * switchActivity will switch to the specified activity, bringing along the user info
     * @param activity the class of the activity to switch to
     */
    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("active_user", activeUser);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }
}
