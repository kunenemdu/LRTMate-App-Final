package com.example.fypmetroapp;

public class Bus_Tracker {
    com.example.fypmetroapp.LatLng position;
    String name;

    public Bus_Tracker () {

    }

    public com.example.fypmetroapp.LatLng getPosition() {
        return position;
    }

    public void setPosition(com.example.fypmetroapp.LatLng position) {
        this.position = position;
    }

    public Bus_Tracker(com.example.fypmetroapp.LatLng position, String name) {
        this.position = position;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
