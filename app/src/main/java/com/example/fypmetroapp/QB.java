package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class QB extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Quatre Bornes Station";
        station.type = "BUS";
        station.position = new LatLng(-20.2653072, 57.4783713);
        station.buses = getBuses();
        return station;
    }

    @Override
    public ArrayList<Bus> getBuses() {
        ArrayList<Bus> buses = new ArrayList<>();
        buses.add(new Bus_153());
        buses.add(new Bus_163());
        buses.add(new Bus_3());
        return buses;
    }

    @Override
    public void setBuses(ArrayList<Bus> buses) {
        super.setBuses(buses);
    }
}