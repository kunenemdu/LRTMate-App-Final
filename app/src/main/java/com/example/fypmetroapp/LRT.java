package com.example.fypmetroapp;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class LRT {
    String name;
    ArrayList<Station> stops;
    Polyline busPolyline;

    public Polyline getBusPolyline() {
        return busPolyline;
    }

    public void setBusPolyline(Polyline busPolyline) {
        this.busPolyline = busPolyline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Station> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Station> stops) {
        this.stops = stops;
    }
}
