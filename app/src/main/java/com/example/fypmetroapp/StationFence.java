package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class StationFence {
    String stationName;
    LatLng stationLocation;
    String type;

    public StationFence (String stationName, LatLng stationLocation, String type) {
        this.stationName = stationName;
        this.stationLocation = stationLocation;
        this.type = type;
    }

    public StationFence() {

    }
}
