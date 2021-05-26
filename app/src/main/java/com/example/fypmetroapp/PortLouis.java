package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class PortLouis extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "Port Louis Victoria";
        station.type = "L";
        station.position = new LatLng(-20.1625125, 57.4982089);
        return station;
    }
}
