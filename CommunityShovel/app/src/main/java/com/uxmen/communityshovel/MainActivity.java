package com.uxmen.communityshovel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {
    private static String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";
    private SupportMapFragment mapFragment;
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private Button viewCommentsButton;
    private Button volunteerButton;
    private ImageButton upvoteButton;
    private User activeUser;
    private HashMap<String, Request> requests = new HashMap<String, Request>();
    private GoogleMap map;
    private Boolean selectionVisible = false;
    private Marker curMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            String s = new String(savedInstanceState.getString(KEY));
            Log.d(DEBUG, s);
        }

        activeUser = getIntent().getParcelableExtra("active_user");
        Log.d(DEBUG, "Main Activity: bio = " + activeUser.getBio());
        
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        viewCommentsButton = (Button) findViewById(R.id.selection_view_comments_button);
        volunteerButton = (Button) findViewById(R.id.selection_volunteer_button);
        upvoteButton = (ImageButton) findViewById(R.id.selection_upvote_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        viewCommentsButton.setOnClickListener(this);
        volunteerButton.setOnClickListener(this);
        upvoteButton.setOnClickListener(this);

        // hide selection initially
        findViewById(R.id.selection).setVisibility(View.INVISIBLE);
    }

    public void onMapReady(GoogleMap map) {

        // for each request:
        this.map = map;
        final LatLng curLocation = new LatLng(40.110513, -88.219336);

        showRequests(map);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 14));
        // Set a listener for marker click.
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
    }

    public void showRequests(GoogleMap map) {
        String url ="http://10.0.2.2:5000/get-all-requests";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(DEBUG, response.toString());

                            Iterator<String> keys = response.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();

                                if (response.get(key) instanceof JSONObject) {
                                    try {
                                        JSONObject request = (JSONObject)response.get(key);
                                        Log.d(DEBUG, request.toString());
                                        Log.d(DEBUG, "key: " + key);
                                        String requestId = key;
                                        String creatorId = request.getString("creator_id");
                                        String info = request.getString("info");

                                        ArrayList<String> volunteers = new ArrayList<String>();
                                        ArrayList<String> comments = new ArrayList<String>();
                                        // we want to catch cases with 0 volunteers or comments, and still add request to list
                                        try {
                                            volunteers.add(request.getString("volunteers"));
                                        } catch (JSONException e) {
                                            Log.d(DEBUG, e.getMessage());
                                        }
                                        try {
                                            comments.add(request.getString("comments"));
                                        } catch (JSONException e) {
                                            Log.d(DEBUG, e.getMessage());
                                        }

                                        int time = request.getInt("time");
                                        int upvotes = request.getInt("upvotes");
                                        double xCoord = request.getDouble("x_coord");
                                        double yCoord = request.getDouble("y_coord");
                                        Request r = new Request(requestId, creatorId, info, volunteers, comments, time, upvotes, xCoord, yCoord);
                                        requests.put(key, r);

                                        final LatLng loc = new LatLng(xCoord, yCoord);
                                        Log.d(DEBUG, loc.toString());
                                        // add marker for request
                                        Marker marker = map.addMarker(new MarkerOptions()
                                                .position(loc)
                                                .title("Request")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.shovel)));
                                        marker.setTag(key);
                                    } catch (JSONException e) {
                                        Log.e("JSON Exception", e.getMessage());
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                    }
                });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        moveCameraToMarker(marker, 0.25);
        showSelection(marker);
        // return true to override default marker click action
        return true;
    }

    public void onMapClick(LatLng point) {

        if (this.selectionVisible && this.curMarker != null) {
            int containerHeight = ((View) findViewById(R.id.map)).getHeight();
            Point pointScreenPos = this.map.getProjection().toScreenLocation(point);
            if (pointScreenPos.y < containerHeight / 2) {
                hideSelection();
            }
        }
    }

    public void moveCameraToMarker(Marker marker, double yOffset) {
        int containerHeight = ((View)findViewById(R.id.map)).getHeight();
        Projection projection = this.map.getProjection();

        LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude);

        Point markerScreenPos = projection.toScreenLocation(markerLatLng);
        Point newMarkerPoint = new Point(markerScreenPos.x,
                markerScreenPos.y + (int)(containerHeight * yOffset));

        LatLng newMarkerLatLng = projection
                .fromScreenLocation(newMarkerPoint);

        map.animateCamera(CameraUpdateFactory.newLatLng(newMarkerLatLng), 500, null);
    }

    public void showSelection(Marker marker) {
        this.curMarker = marker;
        Request request;
        // find request based on the provided key
        try {
            request = requests.get((String)marker.getTag());
        } catch (Exception e) {
            Log.e("requests.get Error", e.getMessage());
            return;
        }
        Log.d(DEBUG, request.getInfo());
        // populate selection overlay with request details
        TextView textViewSelectionLocation = (TextView) findViewById(R.id.selection_location_text);
        TextView textViewSelectionUpvotes = (TextView) findViewById(R.id.selection_upvotes_text);
        TextView textViewSelectionInfo = (TextView) findViewById(R.id.selection_info_text);

        Context context = this;
        Geocoder geo = new Geocoder(context);
        String selectionLocation = "";
        try {
            List<Address> markerAddress = geo.getFromLocation(marker.getPosition().latitude,
                    marker.getPosition().longitude, 1);
            selectionLocation = markerAddress.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.d(DEBUG, "Could not find address");
        }

        textViewSelectionLocation.setText(selectionLocation);
        textViewSelectionUpvotes.setText(String.valueOf(request.getUpvotes()));
        textViewSelectionInfo.setText(request.getInfo());

        // make selection overlay visible
        if (!this.selectionVisible) {
            Animation slideUp = AnimationUtils.loadAnimation(this,
                    R.anim.slide_up);
            View selectionView = findViewById(R.id.selection);
            selectionView.startAnimation(slideUp);
            selectionView.setVisibility(View.VISIBLE);
            this.selectionVisible = true;
        }

        Log.d(DEBUG, "requestId: " + request.getRequestId());
        // add onClick listener for empty space?
    }

    public void hideSelection() {
        if (this.selectionVisible) {
            Animation slideDown = AnimationUtils.loadAnimation(this,
                    R.anim.slide_down);
            View selectionView = findViewById(R.id.selection);
            selectionView.startAnimation(slideDown);
            selectionView.setVisibility(View.INVISIBLE);
            this.selectionVisible = false;
            moveCameraToMarker(this.curMarker, 0.0);
        }
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
        } else if (this.selectionVisible && v.getId() == R.id.selection_view_comments_button) {
            Log.d(DEBUG, "Viewing comments for selection");
        } else if (this.selectionVisible && v.getId() == R.id.selection_volunteer_button) {
            Log.d(DEBUG, "Volunteering for selection");
            confirmVolunteer();
        } else if (this.selectionVisible && v.getId() == R.id.selection_upvote_button) {
            Log.d(DEBUG, "Upvoting selection");
            upvoteSelection();
            TextView upvotesView = (TextView) findViewById(R.id.selection_upvotes_text);
            upvotesView.setText(String.valueOf(Integer.parseInt((String)upvotesView.getText()) + 1));
            Request curRequest = requests.get(Integer.parseInt((String)this.curMarker.getTag()));
            curRequest.setUpvotes(curRequest.getUpvotes() + 1);
        }
    }

    public void upvoteSelection() {
        String url;
        // find request based on the provided key
        try {
            url = "http://10.0.2.2:5000/upvote-request/" + this.curMarker.getTag();
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

    public void confirmVolunteer() {
        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to volunteer for this project?");

        // Set Alert Title
        builder.setTitle("Alert");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.
        builder.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(DEBUG, "Confirming volunteer for request");
                    }
                });

        // Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.
        builder.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(DEBUG, "Canceling volunteer for request");
                        //dialog.cancel();
                    }
                });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
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