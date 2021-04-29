package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class QB extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Quatre Bornes Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.2653072, 57.4783713);
        return station;
    }
}