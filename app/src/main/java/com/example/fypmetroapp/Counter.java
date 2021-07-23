package com.example.fypmetroapp;

public class Counter {
    int occupancy;
    String name;

    public Counter(String name, int occupancy) {
        this.name = name;
        this.occupancy = occupancy;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Counter() {

    }
}
