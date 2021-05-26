package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Barkly extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "Barkly";
        station.type = "L";
        station.position = new LatLng(-20.2209104, 57.4584639);
        return station;
    }
}
