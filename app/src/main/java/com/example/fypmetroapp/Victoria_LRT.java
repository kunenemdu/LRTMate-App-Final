package com.example.fypmetroapp;

import java.util.ArrayList;

public class Victoria_LRT extends LRT {
    @Override
    public String getName() {
        return "Towards Rose Hill";
    }

    @Override
    public ArrayList<Station> getStops() {
        ArrayList<Station> busStops = new ArrayList<>();
        busStops.add(new PortLouis().addStation());
        busStops.add(new StLouis().addStation());
        busStops.add(new Corom().addStation());
        busStops.add(new Barkly().addStation());
        busStops.add(new BeauB().addStation());
        busStops.add(new Vander().addStation());
        busStops.add(new RoseHill().addStation());
        return busStops;
    }

    @Override
    public void setName(String name) {
        this.name = "Towards Rose Hill";
    }

    @Override
    public void setStops(ArrayList<Station> stops) {
        this.stops = getStops();
    }
}
