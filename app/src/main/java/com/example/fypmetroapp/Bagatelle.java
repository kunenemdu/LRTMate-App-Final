package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Bagatelle extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Bagatelle Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.2241773, 57.4938127);
        station.buses = getBuses();
        station.occupancy = 22;
        return station;
    }

    @Override
    public ArrayList<Bus> getBuses() {
        ArrayList<Bus> buses = new ArrayList<>();
        buses.add(new Bus_163());
        return buses;
    }

    @Override
    public void setBuses(ArrayList<Bus> buses) {
        super.setBuses(buses);
    }
}