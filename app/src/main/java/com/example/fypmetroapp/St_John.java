package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class St_John extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "St John";
        station.type = "BUS";
        station.position = new LatLng(-20.2164001, 57.4694824);
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
        super.setBuses(buses);
    }
}
