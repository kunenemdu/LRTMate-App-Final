package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Pailles extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Pailles Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.1847028, 57.4816405);
        station.buses = getBuses();
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