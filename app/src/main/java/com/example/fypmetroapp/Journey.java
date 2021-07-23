package com.example.fypmetroapp;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsResult;

public class Journey {
    DirectionsResult origin_walk, destination_walk;
    Polyline ori_walk_poly, dest_walk_poly, my_poly;
    boolean origin_to_station_done, station_to_destination_done, mine_done;

    public Journey () {}

    public boolean isOrigin_to_station_done() {
        return origin_to_station_done;
    }

    public void setOrigin_to_station_done(boolean origin_to_station_done) {
        this.origin_to_station_done = origin_to_station_done;
    }

    public boolean isStation_to_destination_done() {
        return station_to_destination_done;
    }

    public void setStation_to_destination_done(boolean station_to_destination_done) {
        this.station_to_destination_done = station_to_destination_done;
    }

    public boolean isMine_done() {
        return mine_done;
    }

    public void setMine_done(boolean mine_done) {
        this.mine_done = mine_done;
    }

    public DirectionsResult getOrigin_walk() {
        return origin_walk;
    }

    public void setOrigin_walk(DirectionsResult origin_walk) {
        this.origin_walk = origin_walk;
    }

    public DirectionsResult getDestination_walk() {
        return destination_walk;
    }

    public void setDestination_walk(DirectionsResult destination_walk) {
        this.destination_walk = destination_walk;
    }

    public Polyline getOri_walk_poly() {
        return ori_walk_poly;
    }

    public void setOri_walk_poly(Polyline ori_walk_poly) {
        this.ori_walk_poly = ori_walk_poly;
    }

    public Polyline getDest_walk_poly() {
        return dest_walk_poly;
    }

    public void setDest_walk_poly(Polyline dest_walk_poly) {
        this.dest_walk_poly = dest_walk_poly;
    }

    public Polyline getMy_poly() {
        return my_poly;
    }

    public void setMy_poly(Polyline my_poly) {
        this.my_poly = my_poly;
    }
}
