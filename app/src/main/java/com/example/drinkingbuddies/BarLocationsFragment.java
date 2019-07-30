package com.example.drinkingbuddies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API functions!!! Need to make more accessible and add features
public class BarLocationsFragment extends Fragment implements OnMapReadyCallback {

    private final String URL = "https://developers.zomato.com/api/v2.1/";
    private final String API_KEY = "dd755fc4ccc5e5e90d898f165326aa4a";

    // Set to desired location to get bars in area
    private final String LOCATION_QUERY = "Jacksonville";

    public JSONObject jsonObject;
    public JSONArray jsonArray;
    RequestQueue queue;
    JsonObjectRequest jsonObjectRequest;
    List<Bar> barList = new ArrayList<>();
    String cityID;
    String establishmentID = new String();
    GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());

        // Populate barList with all bars found within LOCATION_QUERY
        apiObjectQuery("cities", new String[]{"q"}, new String[]{LOCATION_QUERY});

        return inflater.inflate(R.layout.fragment_bar_locations, container, false);
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
                        else
                            return;

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
                            barList.add(new Bar(name, new LatLng(lat, lon)));
                            Log.v("LatLon", lat + ", " + lon);
                        }

                        for(int i = 0; i < barList.size(); i++) {
                            googleMap.addMarker(new MarkerOptions().position(barList.get(i).location).title(barList.get(i).name));
                        }
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
