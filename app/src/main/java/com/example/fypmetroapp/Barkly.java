package com.example.fypmetroapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Barkly extends Station {
    public Station addStation() {
        Station station = new Station();
        station.name = "Barkly";
        station.type = "L";
        station.position = new LatLng(-20.2209104, 57.4584639);
        station.buses = getBuses();
        station.lines = getLines();
        return station;
    }

    @Override
    public ArrayList<LRT> getLines() {
        ArrayList<LRT> lines = new ArrayList<>();
        lines.add(new PortLouis_LRT());
        lines.add(new RoseH_LRT());
        return lines;
    }

    @Override
    public void setLines(ArrayList<LRT> lines) {
        super.setLines(lines);
    }
}
