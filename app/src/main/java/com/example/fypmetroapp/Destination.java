package com.example.fypmetroapp;

public class Destination {
    Station station_bus;
    Station station_lrt;
    String destination;
    String name;
    String address;
    LatLng position;

    public Destination () {}

    //getters and setters
    //...

    public Station getStation_lrt() {
        return station_lrt;
    }

    public void setStation_lrt(Station station_lrt) {
        this.station_lrt = station_lrt;
    }

    public Station getStation_bus() {
        return station_bus;
    }

    public void setStation_bus(Station station_bus) {
        this.station_bus = station_bus;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
