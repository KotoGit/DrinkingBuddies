package com.example.drinkingbuddies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API functions!!! Need to make more accessible and add features
public class BarLocationsFragment extends Fragment {

    private final String URL = "https://developers.zomato.com/api/v2.1/";
    private final String API_KEY = "dd755fc4ccc5e5e90d898f165326aa4a";
    public JSONObject jsonObject;
    public JSONArray jsonArray;
    private String[] keys, values;
    RequestQueue queue;
    JsonArrayRequest jsonArrayRequest;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());

        List<String> bars = findBars("Jacksonville");
        for(int i = 0; i < bars.size(); i++) {
            Log.v("City", bars.get(i));
        }

        return inflater.inflate(R.layout.fragment_bar_locations, container, false);
    }

    // Saves JSON result to json variable in Fragment
    public void apiArrayQuery(String resource, String[] k, String[] v) {

        keys = k;
        values = v;

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL + resource, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonArray = response;
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

                for(int i = 0; i < keys.length; i++) {
                    headers.put(keys[i], values[i]);
                }

                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    public void apiObjectQuery(String resource, String[] k, String[] v) {

        keys = k;
        values = v;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL + resource, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;
                try {
                    jsonArray = jsonObject.getJSONArray("location_suggestions");
                    Log.v("ARRAY", jsonArray.toString());
                    jsonObject = jsonArray.getJSONObject(0);
                }
                catch(JSONException e) {

                }

                Log.v("CITY", jsonObject.toString());
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

                for(int i = 0; i < keys.length; i++) {
                    headers.put(keys[i], values[i]);
                }

                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public List<String> findBars(String location) {
        List<String> barList = new ArrayList<>();
        String cityID;
        String establishmentID = new String();

        apiObjectQuery("cities", new String[]{"q"}, new String[]{location});

        /*
        try {
            jsonArray = jsonObject.getJSONArray("location_suggestions");
            jsonObject = jsonArray.getJSONObject(0);
            Log.v("CITY", jsonObject.toString());
            cityID = jsonObject.getString("id");

            apiArrayQuery("establishments", new String[]{"city_id"}, new String[]{cityID});
            for(int i = 0; i < jsonArray.length(); i++) {
                if(jsonArray.getJSONObject(i).getString("establishment_name").equalsIgnoreCase("bar"))
                    establishmentID = jsonArray.getJSONObject(i).getString("establishment_id");
            }

            apiArrayQuery("search", new String[]{"entity_id", "entity_type", "establishment_type"}, new String[]{cityID, "city", establishmentID});
            jsonArray = jsonArray.getJSONObject(0).getJSONArray("restaurants");
            for(int i = 0; i < jsonArray.length(); i++) {
                barList.add(jsonArray.getJSONObject(i).getString("name"));
            }

        } catch(JSONException e) {
            return null;
        }*/


        return barList;
    }
}
