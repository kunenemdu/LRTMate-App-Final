package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Reduit extends Station {
    @Override
    public Station addStation() {
        Station station = new Station();
        station.name = "Reduit";
        station.type = "B";
        station.position = new LatLng(-20.232312, 57.498751);
        station.buses = getBuses();
        station.connects_to = getConnects_to();
        return station;
    }

    @Override
    public ArrayList<Bus> getConnects_to() {
        ArrayList<Bus> connects = new ArrayList<>();
        connects.add(new Bus_153());
        connects.add(new Bus_163());
        return connects;
    }

    @Override
    public void setConnects_to(ArrayList<Bus> connects_to) {
        super.setConnects_to(getConnects_to());
    }

    @Override
    public ArrayList<Bus> getBuses() {
        ArrayList<Bus> buses = new ArrayList<>();
        buses.add(new Bus_153());
        buses.add(new Bus_163());
        return buses;
    }

    @Override
    public void setBuses(ArrayList<Bus> buses) {
        super.setBuses(getBuses());
    }
}
