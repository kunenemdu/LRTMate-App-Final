package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BeauBassin extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Beau Bassin";
        station.type = "BUS";
        station.position = new LatLng(-20.2231152, 57.4681413);
        station.buses = getBuses();
        return station;
    }

    @Override
    public ArrayList<Bus> getBuses() {
        ArrayList<Bus> buses = new ArrayList<>();
        buses.add(new Bus_3());
        return buses;
    }

    @Override
    public void setBuses(ArrayList<Bus> buses) {
        this.buses = getBuses();
    }
}
