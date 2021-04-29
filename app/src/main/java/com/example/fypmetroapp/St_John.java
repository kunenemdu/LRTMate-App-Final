package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class St_John extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "St John";
        station.type = "BUS";
        station.position = new LatLng(-20.2164001, 57.4694824);
        return station;
    }
}
