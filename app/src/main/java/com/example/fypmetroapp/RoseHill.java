package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RoseHill extends Station{
    public Station addStation() {
        Station station = new Station();
        station.name = "Rose Hill Central";
        station.type = "L";
        station.position = new LatLng(-20.2421818, 57.4758875);
        return station;
    }
}
