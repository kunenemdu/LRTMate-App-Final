package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Kennedy extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Kennedy Street";
        station.type = "BUS";
        station.position = new LatLng(-20.2417338, 57.475571);
        station.buses = getBuses();
        station.occupancy = 42;
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
        super.setBuses(buses);
    }
}
