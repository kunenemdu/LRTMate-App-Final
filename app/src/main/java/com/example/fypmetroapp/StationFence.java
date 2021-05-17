package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class StationFence {
    String stationName;
    LatLng stationLocation;
    String type;
    int distance;

    public StationFence (String stationName, LatLng stationLocation, String type, int distance) {
        this.stationName = stationName;
        this.stationLocation = stationLocation;
        this.type = type;
        this.distance = distance;
    }

    public StationFence() {

    }
}
