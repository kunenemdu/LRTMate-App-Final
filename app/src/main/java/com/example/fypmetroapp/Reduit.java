package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Reduit extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Reduit";
        station.type = "B";
        station.position = new LatLng(-20.232312, 57.498751);
        return station;
    }
}
