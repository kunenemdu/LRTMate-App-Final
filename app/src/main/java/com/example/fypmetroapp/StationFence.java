package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class StationFence {
    String stationName;
    LatLng stationLocation;

    public StationFence () {}

    public StationFence (String stationName, LatLng stationLocation) {
        this.stationName = stationName;
        this.stationLocation = stationLocation;
    }
}
