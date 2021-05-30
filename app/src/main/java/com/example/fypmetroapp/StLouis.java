package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StLouis extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "St Louis";
        station.type = "L";
        station.position = new LatLng(-20.180942, 57.4767888);
        station.buses = getBuses();
        return station;
    }
}
