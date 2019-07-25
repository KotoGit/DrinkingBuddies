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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// API functions!!! Need to make more accessible and add features
public class BarLocationsFragment extends Fragment {

    private final String url = "https://developers.zomato.com/api/v2.1/cities";
    private final String API_KEY = "dd755fc4ccc5e5e90d898f165326aa4a";
    RequestQueue queue;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
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

        Log.v("SUCCESS", "LOADED");
        return inflater.inflate(R.layout.fragment_bar_locations, container, false);
    }
}
