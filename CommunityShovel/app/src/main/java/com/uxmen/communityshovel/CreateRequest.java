package com.uxmen.communityshovel;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateRequest extends AppCompatActivity implements GoogleMap.OnMapClickListener, View.OnClickListener,
        OnMapReadyCallback {
    private static final String DEBUG = "DEBUG";
    private static final String KEY = "KEY";
    private static final String DATA = "We passed the bundle of data";
    private SupportMapFragment mapFragment;
    private ImageButton homeButton;
    private ImageButton createRequestButton;
    private ImageButton profileButton;
    private Button cancelButton;
    private Button confirmButton;
    private EditText location;
    private EditText info;
    //private ArrayList<Request> requests = new ArrayList<Request>();
    private GoogleMap map;
    private Marker mapMarker;
    private User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate()");
        setContentView(R.layout.activity_create_request);

        if (savedInstanceState != null) {
            String s = new String(savedInstanceState.getString(KEY));
            Log.d(DEBUG, s);
        }

        activeUser = getIntent().getParcelableExtra("active_user");
        Log.d(DEBUG, "CreateRequest Activity: email = " + activeUser.getEmail());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        homeButton = (ImageButton) findViewById(R.id.home_button);
        createRequestButton = (ImageButton) findViewById(R.id.create_request_button);
        profileButton = (ImageButton) findViewById(R.id.profile_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        location = (EditText) findViewById(R.id.location_text);
        info = (EditText) findViewById(R.id.info_text);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

    }

    public void onMapReady(GoogleMap map) {
        // TODO: show all other requests while you are creating, in case you forgot which existed
        this.map = map;
        final LatLng curLocation = new LatLng(40.1164, -88.2434);
        mapMarker = map.addMarker(new MarkerOptions()
                .position(curLocation)
                .title("Marker"));
        mapMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mapmarker_highlighted",114,152)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 14));
        // Set a listener for map click.
        map.setOnMapClickListener(this);

        Geocoder geo = new Geocoder(this);
        try {
            List<Address> markerAddress = geo.getFromLocation(curLocation.latitude,
                    curLocation.longitude, 1);
            location.setText(markerAddress.get(0).getAddressLine(0));
        } catch (IOException e) {
            Log.d(DEBUG, "Could not find address");
            Log.d(DEBUG, e.getMessage());
            location.setText("Could not find address");
        }


    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    // This method was changed to push the new request and return to the MainActivity map
    public void newRequest(LatLng point, View v) {
//        Request r = new Request(activeUser.getFirstName(), info.getText().toString(),
//                new ArrayList<String>(), new ArrayList<String>(), 0, 0, point.longitude, point.latitude);

        String url ="http://10.0.2.2:5000/create-request";

//      Creates a request through JSON body. Needs creator_id, info, time, x_coord, y_coord, and comments
        JSONObject request = new JSONObject();
        try{
            request.put("creator_id", activeUser.getEmail());
            request.put("info", info.getText().toString());
            request.put("time", 0);
            request.put("x_coord", point.latitude);
            request.put("y_coord", point.longitude);
            request.put("upvotes", 0);
//            request.put("comments", "0");
            // empty comments?
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
        }

        Log.d(DEBUG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (com.android.volley.Request.Method.POST, url, request, response -> {
                    Log.d(DEBUG, response.toString());
                    // TODO: focus map on newly created request
                    switchActivity(MainActivity.class);
                }, error -> {
                    if(error.networkResponse != null) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                    Toast.makeText(v.getContext(), "Error Creating Request", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.home_button || v.getId() == R.id.cancel_button) {
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.create_request_button) {
            Toast.makeText(this, "Already Creating Request", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.confirm_button) {
            newRequest(mapMarker.getPosition(), v);
            switchActivity(MainActivity.class);
        }
    }

    public void onMapClick(LatLng point) {
        //mapMarker.setPosition(point);
        mapMarker.remove();
        mapMarker = map.addMarker(new MarkerOptions()
                .position(point)
                .title("Marker"));
        mapMarker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mapmarker_highlighted",114,152)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14));

        Geocoder geo = new Geocoder(this);
        try {
            List<Address> markerAddress = geo.getFromLocation(point.latitude,
                    point.longitude, 1);
            location.setText(markerAddress.get(0).getAddressLine(0));
        } catch (IOException e) {
            Log.d(DEBUG, "Could not find address");
            Log.d(DEBUG, e.getMessage());
            Log.d(DEBUG, Double.toString(point.latitude));
            Log.d(DEBUG, Double.toString(point.longitude));
            location.setText("Could not find address");
        }

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
