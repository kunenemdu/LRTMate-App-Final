package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Station {
    int distance;
    String name;
    LatLng position;
    String type;

    public Station addStation () {
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public Station setStations (String name, String type, LatLng position) {
        Station station = new Station();
        station.name = name;
        station.type = type;
        station.position = position;
        return station;
    }
}
