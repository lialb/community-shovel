package com.uxmen.communityshovel;

import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class CreateRequest extends AppCompatActivity implements GoogleMap.OnMapClickListener, View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
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
    private Button cancelButton;
    private Button confirmButton;
    private String location;
    private String info;
    private GoogleMap map;
    private Marker mapMarker;
    private User activeUser;

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
        cancelButton = (Button) findViewById(R.id.cancel_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        homeButton.setOnClickListener(this);
        createRequestButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        viewCommentsButton.setOnClickListener(this);
        volunteerButton.setOnClickListener(this);
        upvoteButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        // hide selection initially
        findViewById(R.id.selection).setVisibility(View.INVISIBLE);
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;
        final LatLng curLocation = new LatLng(40.1164, -88.2434);
        //mapMarker = map.addMarker(new MarkerOptions()
        //        .position(curLocation)
        //        .title("Marker")
        //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shovel)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 14));
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
    }

    public void onMapClick(LatLng point) {
        mapMarker.setPosition(point);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

        Geocoder geo = new Geocoder(this);
        String selectionLocation = "";
        try {
            List<Address> markerAddress = geo.getFromLocation(point.latitude,
                    point.longitude, 1);
            selectionLocation = markerAddress.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.d(DEBUG, "Could not find address");
        }
        TextView textViewLocation = (TextView) findViewById(R.id.location_text);
        textViewLocation.setText(selectionLocation);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.home_button || v.getId() == R.id.cancel_button) {
            switchActivity(MainActivity.class);
        } else if (v.getId() == R.id.profile_button) {
            switchActivity(YourProfile.class);
        } else if (v.getId() == R.id.confirm_button || v.getId() == R.id.create_request_button) {
            //switchActivity(Request.class);
        }
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        moveCameraToMarker(marker, 0.25);
        // return true to override default marker click action
        return true;
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
