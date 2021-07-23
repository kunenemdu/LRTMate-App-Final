package com.example.fypmetroapp;

public class Favourite {
    String destination;
    String name;
    String address;
    com.example.fypmetroapp.LatLng position;

    public Favourite(String destination, String name, String address, LatLng position) {
        this.destination = destination;
        this.name = name;
        this.address = address;
        this.position = position;
    }

    public com.example.fypmetroapp.LatLng getPosition() {
        return position;
    }

    public void setPosition(com.example.fypmetroapp.LatLng position) {
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
