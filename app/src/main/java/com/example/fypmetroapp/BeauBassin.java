package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

public class BeauBassin extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Beau Bassin";
        station.type = "BUS";
        station.position = new LatLng(-20.2231152, 57.4681413);
        return station;
    }
}
