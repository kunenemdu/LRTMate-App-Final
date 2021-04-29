package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Kennedy extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Kennedy Street";
        station.type = "BUS";
        station.position = new LatLng(-20.2417338, 57.475571);
        return station;
    }
}
