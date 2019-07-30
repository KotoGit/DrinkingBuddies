package com.example.drinkingbuddies;

import com.google.android.gms.maps.model.LatLng;

public class Bar {
    public String name;
    public LatLng location;

    private Bar() {
        this("", new LatLng(0, 0));
    }

    public Bar(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }
}
