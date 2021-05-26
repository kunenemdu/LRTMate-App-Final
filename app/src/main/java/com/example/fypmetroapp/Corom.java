package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class Corom extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "Coromandel";
        station.type = "L";
        station.position = new LatLng(-20.1837264, 57.4693912);
        return station;
    }
}
