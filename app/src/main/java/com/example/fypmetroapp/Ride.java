package com.example.fypmetroapp;

import java.util.ArrayList;

public class Ride {
    int num_stops;
    ArrayList<Station> stations;

    public int getNum_stops() {
        return num_stops;
    }

    public void setNum_stops(int num_stops) {
        this.num_stops = num_stops;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }
}
