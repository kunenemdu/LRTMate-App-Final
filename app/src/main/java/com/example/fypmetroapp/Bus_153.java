package com.example.fypmetroapp;

import java.util.ArrayList;
import java.util.List;

public class Bus_153 extends Bus{
    @Override
    public int getName() {
        return 153;
    }

    @Override
    public ArrayList<Station> getStops() {
        ArrayList<Station> busStops = new ArrayList<>();
        busStops.add(new QB().addStation());
        busStops.add(new Reduit().addStation());
        busStops.add(new Moka().addStation());
        return busStops;
    }
}
