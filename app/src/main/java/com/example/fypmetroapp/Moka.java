package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Moka extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "MGI Bus Station";
        station.type = "BUS";
        station.position = new LatLng(-20.226135, 57.506561);
        station.buses = getBuses();
        return station;
    }

    @Override
    public ArrayList<Bus> getBuses() {
        ArrayList<Bus> buses = new ArrayList<>();
        buses.add(new Bus_153());
        return buses;
    }

    @Override
    public void setBuses(ArrayList<Bus> buses) {
        this.buses = getBuses();
    }
}