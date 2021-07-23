package com.example.fypmetroapp;

public class Origin {
    String origin;
    String name;
    String address;
    LatLng position;
    Station station_bus;
    Station station_lrt;

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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
