package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Victoria extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Victoria Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.1629758, 57.4979997);
        return station;
    }
}
