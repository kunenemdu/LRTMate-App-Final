package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Moka extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "MGI Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.226135, 57.506561);
        return station;
    }
}