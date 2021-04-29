package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LRT_Stations {
    public ArrayList<Station> allStations () {
        Station station = new Station();
        ArrayList<Station> stats = new ArrayList<>();
        stats.add(station);
        return stats;
    }
}
