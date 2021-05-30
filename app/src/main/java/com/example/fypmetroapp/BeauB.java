package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BeauB extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "Beau Bassin";
        station.type = "L";
        station.position = new LatLng(-20.2266891, 57.4673957);
        station.buses = getBuses();
        return station;
    }
}
