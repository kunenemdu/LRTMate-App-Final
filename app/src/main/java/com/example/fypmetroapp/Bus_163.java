package com.example.fypmetroapp;

import java.util.ArrayList;
import java.util.List;

public class Bus_163 extends Bus {
    @Override
    public int getName() {
        return 163;
    }

    @Override
    public ArrayList<Station> getStops() {
        ArrayList<Station> busStops = new ArrayList<>();
        busStops.add(new QB().addStation());
        busStops.add(new Reduit().addStation());
        busStops.add(new Bagatelle().addStation());
        busStops.add(new Pailles().addStation());
        busStops.add(new Victoria().addStation());
        return busStops;
    }
}
