package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Bagatelle extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Bagatelle Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.2241773, 57.4938127);
        return station;
    }
}