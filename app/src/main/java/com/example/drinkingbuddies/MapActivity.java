package com.example.drinkingbuddies;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.mbms.DownloadRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.drinkingbuddies.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermissions) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }

    }

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean checkPermissions = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private GeoApiContext mGeoApiContext = null;
    private DirectionsApiRequest mDirectionsApiRequest = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();
        Log.d(TAG, "getLocationPermission ran");


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET}, 10);


    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(checkPermissions){
                final com.google.android.gms.tasks.Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                            Address address = null;

                            Log.d(TAG, "found Location");
                            Location currentLocation = (Location) task.getResult();
                            mMap.getUiSettings().setZoomControlsEnabled(true);

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 9f);

                            LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            // Set jacksonville (ending LatLng) to position sent in from BarLocationsFragment
                            LatLng jacksonville = new LatLng(getIntent().getDoubleExtra("latitude", 30.3322), getIntent().getDoubleExtra("longitude", -81.6567));                        //this will be the destination, change
                            mMap.addMarker(new MarkerOptions().position(jacksonville));
                            mMap.addMarker(new MarkerOptions().position(current));

                            Geocoder geocoder = new Geocoder(MapActivity.this);
                            List<Address> list = new ArrayList<>();

                            try{
                                list = geocoder.getFromLocation(jacksonville.latitude, jacksonville.longitude, 1);
                                Log.d(TAG, "try block executed");
                            }catch (IOException e){
                                Log.d(TAG, e.toString());
                            }
                            if(list.size() > 0){
                                address = list.get(0);
                                Log.d(TAG, "Address that was gotten is: " + address.toString());
                            }

                            //String goGetUrl = urlHelper(current, jacksonville, "driving");

                            getDirections(current, jacksonville);



                        }
                        else{
                            Log.d(TAG, "current location null");
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation security exception");
        }
    }


    private void finishDirections(final DirectionsResult r){
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run(){
                Log.d(TAG, "finish directions/run has ran");

                for(DirectionsRoute cycle : r.routes){
                    List<com.google.maps.model.LatLng> thisIsForPaths = PolylineEncoding.decode(cycle.overviewPolyline.getEncodedPath());
                    List<LatLng> change = new ArrayList<>();

                    for(com.google.maps.model.LatLng ll : thisIsForPaths){
                        change.add(new LatLng(ll.lat,ll.lng));
                    }
                    Polyline pl = mMap.addPolyline(new PolylineOptions().addAll(change));
                    pl.setColor(ContextCompat.getColor(getApplicationContext(), R.color.wallet_holo_blue_light));
                }
            }
        });
    }

    private void getDirections(LatLng start, LatLng end){
        mDirectionsApiRequest = DirectionsApi.newRequest(mGeoApiContext).origin(new com.google.maps.model.LatLng(start.latitude, start.longitude))
                .destination(new com.google.maps.model.LatLng(end.latitude,end.longitude)).mode(TravelMode.DRIVING);
        Log.d(TAG,"getDirections called");
        mDirectionsApiRequest.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                finishDirections(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, e.toString());
            }
        });
        //mDirectionsApiRequest.alternatives(true);
    }


    private String urlHelper(LatLng start, LatLng finish, String walkOrDrive){
        String origin = "origin=" + start.latitude + "," + start.longitude;
        String destination = "destination=" + finish.latitude + "," + finish.longitude;
        String mode = "mode=" + walkOrDrive;
        String param = origin + "&" + destination + "&" + mode;
        String converter = "json";
        String finalUrl = "https://maps.googleapis.com/maps/api/directions/" + converter + "?" + param + "&key=" + getString(R.string.google_maps_API_key);

        return finalUrl;
    }


    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "Moving camera to lat: " + latLng.latitude + " , lon: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_API_key)).build();
        }
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                checkPermissions = true;
                initMap();                                                                              //this may cause crash when ran on new phone, initMap is called twice
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermissions = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkPermissions = true;
                    initMap();
                }
        }
        initMap();
        Log.d(TAG, "initMap ran");


    }
}
