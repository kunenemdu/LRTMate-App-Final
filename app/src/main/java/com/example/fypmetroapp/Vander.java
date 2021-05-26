package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Vander extends Station{
    public Station addStation() {
        Station station = new Station();
        station.name = "Vandersmeech";
        station.type = "L";
        station.position = new LatLng(-20.2354926, 57.473157);
        return station;
    }
}
