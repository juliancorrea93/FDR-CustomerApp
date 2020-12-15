package com.correajulian.demo;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    /*
    TODO: work on getting the request to populate might need to move request to end of the previous activity
        and then put in a bundle to bring over with intent
     */

    private GoogleMap mMap;
    GPSTracker gps;

    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try {

            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[] {mPermission}, 2);

                // If any permission above not allowed by user, this condition will
                // execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(MapsActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onMapReady(mMap);
                Intent intent = new Intent(MapsActivity.this, FinishOrder.class);
                intent.putExtra("email", getIntent().getExtras().getString("email"));
                intent.putExtra("cart", getIntent().getExtras().getString("cart"));
                MapsActivity.this.startActivity(intent);
                MapsActivity.this.finish();
            }
        }, 15000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RequestQueue queue = Volley.newRequestQueue(this);


        String req = "http://192.168.86.35:8080/getloc";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, req, null, response -> {
            System.out.println(response);

            try {
                double robotlat;
                double robotlong;
                robotlat = response.getDouble("latitude");
                robotlong = response.getDouble("longitude");

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();


                System.out.println(latitude);
                System.out.println(longitude);
                System.out.println(robotlat);
                System.out.println(robotlong);

                LatLng myLoc = new LatLng(latitude, longitude);
                LatLng robot = new LatLng(robotlat, robotlong);
                mMap.addMarker(new MarkerOptions().position(myLoc).title("You are here"));
                mMap.addMarker(new MarkerOptions().position(robot).title("Robot is here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));

            } catch (JSONException e) {
                System.out.println("Problem casting lat and lon to double");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Bummer! A Volley error has been triggered. Please close and reopen this app.");
            }
        });

        queue.add(jsonObjectRequest);
    }

}