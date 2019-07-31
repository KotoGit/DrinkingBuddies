package com.example.drinkingbuddies;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API functions!!! Need to make more accessible and add features
public class BarLocationsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private final String URL = "https://developers.zomato.com/api/v2.1/";
    private final String API_KEY = "dd755fc4ccc5e5e90d898f165326aa4a";
    private Dialog favOrMapDialog;
    private Button favSetButton, dirButton;
    private String currentUser;

    // Set to desired location to get bars in area
    private String locationQuery = "Jacksonville";

    public JSONObject jsonObject;
    public JSONArray jsonArray;
    RequestQueue queue;
    JsonObjectRequest jsonObjectRequest;
    List<Bar> barList = new ArrayList<>();
    String cityID;
    String establishmentID = new String();
    GoogleMap googleMap;
    ProgressBar progressBar;

    private Button searchButton;
    private EditText locationInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());

        View view = inflater.inflate(R.layout.fragment_bar_locations, container, false);

        favOrMapDialog = new Dialog(this.getContext());
        favOrMapDialog.setContentView(R.layout.marker_dialog);
        favSetButton = (Button)favOrMapDialog.findViewById(R.id.button_add_fav);
        dirButton = (Button)favOrMapDialog.findViewById(R.id.button_get_dir);

        currentUser = getArguments().getString("user");

        searchButton = view.findViewById(R.id.search_button);
        locationInput = view.findViewById(R.id.location_input);
        progressBar = view.findViewById(R.id.loading_indicator);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                locationQuery = locationInput.getText().toString();
                Log.v("query", locationQuery);
                // Populate barList with all bars found within LOCATION_QUERY (AFTER MAP LOADS)
                barList.clear();
                googleMap.clear();
                apiObjectQuery("cities", new String[]{"q"}, new String[]{locationQuery});
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create and display map
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.bar_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnInfoWindowClickListener(this);
    }

    // Clicking info card will pull up directions
    @Override
    public void onInfoWindowClick(Marker marker) {
        Bar bar = (Bar)marker.getTag();
        favOrMapDialog.setCancelable(false);
        favOrMapDialog.show();
        favSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barName = bar.name;
                Cursor myCursor;
                String[] myProjection = {BarProvider.COLUMN_USERNAME, BarProvider.COLUMN_FAVLIST};
                String mySelection = BarProvider.COLUMN_USERNAME + " = ? ";
                String[] mySelectionArgs = {currentUser};
                try{
                    myCursor = getActivity().getApplicationContext().getContentResolver().query(BarProvider.CONTENT_URI_LOG, myProjection, mySelection, mySelectionArgs, null);
                    if(myCursor.moveToFirst()){
                        String currentFav = myCursor.getString(myCursor.getColumnIndex(BarProvider.COLUMN_FAVLIST));
                        myCursor.close();
                        if(currentFav.compareTo("") != 0 && !currentFav.contains(barName)){
                            ContentValues cvs = new ContentValues();
                            cvs.put(BarProvider.COLUMN_FAVLIST, currentFav + "," + barName);
                            int result = getActivity().getApplicationContext().getContentResolver().update(BarProvider.CONTENT_URI_LOG, cvs, mySelection, mySelectionArgs);
                        }
                        else if(currentFav.contains(barName)){
                            //do nothing
                        }
                        else{
                            ContentValues cvs = new ContentValues();
                            cvs.put(BarProvider.COLUMN_FAVLIST, barName);
                            int result = getActivity().getApplicationContext().getContentResolver().update(BarProvider.CONTENT_URI_LOG, cvs, mySelection, mySelectionArgs);
                        }
                    }
                    else{
                        //username is not in database for some reason
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }finally {
                    favOrMapDialog.dismiss();
                }
            }
        });
        dirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Provide latitude and longitude to MapActivity
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("latitude", bar.location.latitude);
                intent.putExtra("longitude", bar.location.longitude);

                // Start MapActivity
                favOrMapDialog.dismiss();
                startActivity(intent);
            }
        });
    }


    // Saves JSON result to json variable in Fragment
    public void apiObjectQuery(final String resource, String[] k, String[] v) {

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL + resource + addQuery(k, v), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("url",URL + resource + addQuery(k, v));
                jsonObject = response;
                try {
                    // Get cityID
                    if(resource.matches("cities")) {
                        jsonArray = jsonObject.getJSONArray("location_suggestions");

                        if(!jsonArray.isNull(0))
                            jsonObject = jsonArray.getJSONObject(0);
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }

                        cityID = jsonObject.getString("id");
                        apiObjectQuery("establishments", new String[]{"city_id"}, new String[]{cityID});
                    }

                    // Get establishmentID
                    else if(resource.matches("establishments")) {
                        jsonArray = jsonObject.getJSONArray("establishments");

                        for(int i = 0; i < jsonArray.length(); i++) {
                            if(jsonArray.getJSONObject(i).getJSONObject("establishment").getString("name").equalsIgnoreCase("bar")) {
                                establishmentID = jsonArray.getJSONObject(i).getJSONObject("establishment").getString("id");
                            }
                        }

                        apiObjectQuery("search", new String[]{"entity_id", "entity_type", "establishment_type"}, new String[]{cityID, "city", establishmentID});
                    }

                    // Get barList
                    else if(resource.matches("search")) {
                        jsonArray = jsonObject.getJSONArray("restaurants");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            String name = jsonArray.getJSONObject(i).getJSONObject("restaurant").getString("name");
                            double lat = jsonArray.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("latitude");
                            double lon = jsonArray.getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getDouble("longitude");
                            int price = jsonArray.getJSONObject(i).getJSONObject("restaurant").getInt("average_cost_for_two");
                            price = price/2;
                            barList.add(new Bar(name, new LatLng(lat, lon)));

                            ContentValues cv = new ContentValues();
                            cv.put(BarProvider.COLUMN_NAME, name);
                            cv.put(BarProvider.COLUMN_LAT, String.valueOf(lat));
                            cv.put(BarProvider.COLUMN_LON, String.valueOf(lon));
                            cv.put(BarProvider.COLUMN_PRICE, price);
                            Uri u = getActivity().getApplicationContext().getContentResolver().insert(BarProvider.CONTENT_URI_LOC, cv);
                            Log.v("LatLng", lat + ", " + lon);
                            Log.v("Price", Integer.toString(price));
                        }

                        progressBar.setVisibility(View.INVISIBLE);

                        // Used to ensure all markers are visible
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for(int i = 0; i < barList.size(); i++) {
                            Marker m = googleMap.addMarker(new MarkerOptions().position(barList.get(i).location).title(barList.get(i).name));
                            m.setTag(barList.get(i));
                            builder.include(barList.get(i).location);
                        }

                        // Zoom to all markers
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }
                }
                catch(JSONException e) {
                    Log.v("ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.v("ERROR", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Accept", "application/json");
                headers.put("user-key", API_KEY);

                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public String addQuery(String[] k, String[] v) {
        StringBuilder sb = new StringBuilder("?");

        for(int i = 0; i < k.length; i++) {
            sb.append(k[i] + "=");
            sb.append(v[i].replace(" ", "%20"));
            if(i < k.length - 1)
                sb.append("&");
        }

        return sb.toString();
    }
}
