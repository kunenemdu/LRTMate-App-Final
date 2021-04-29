package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Pailles extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Pailles Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.1847028, 57.4816405);
        return station;
    }
}