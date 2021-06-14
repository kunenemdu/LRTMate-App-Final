package com.example.fypmetroapp;

import java.util.ArrayList;

public class RoseH_LRT extends LRT {
    @Override
    public String getName() {
        return "Towards Port Louis";
    }

    @Override
    public ArrayList<Station> getStops() {
        ArrayList<Station> lrtStops = new ArrayList<>();
        lrtStops.add(new RoseHill().addStation());
        lrtStops.add(new Vander().addStation());
        lrtStops.add(new BeauB().addStation());
        lrtStops.add(new Barkly().addStation());
        lrtStops.add(new Corom().addStation());
        lrtStops.add(new StLouis().addStation());
        lrtStops.add(new PortLouis().addStation());
        return lrtStops;
    }

    @Override
    public void setName(String name) {
        this.name = "Towards Port Louis";
    }

    @Override
    public void setStops(ArrayList<Station> stops) {
        this.stops = getStops();
    }
}
