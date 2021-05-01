package com.example.fypmetroapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class UserUpdates {
    public Location location;
    public LatLng latLngLocation;
    public LatLng latLngDestination;
    public int covered_dist;
    public int pre_total_dist;
    public int new_total_dist;

    public LatLng getLatLngDestination() {
        return latLngDestination;
    }

    public void setLatLngDestination(LatLng latLngDestination) {
        this.latLngDestination = latLngDestination;
    }

    public int getPre_total_dist() {
        return pre_total_dist;
    }

    public void setPre_total_dist(int pre_total_dist) {
        this.pre_total_dist = pre_total_dist;
    }

    public int getCovered_dist() {
        return covered_dist;
    }

    public void setCovered_dist(int covered_dist) {
        this.covered_dist = covered_dist;
    }

    public int getNew_total_dist() {
        return new_total_dist;
    }

    public void setNew_total_dist(int new_total_dist) {
        this.new_total_dist = new_total_dist;
    }

    public LatLng getLatLngLocation() {
        return latLngLocation;
    }

    public void setLatLngLocation(LatLng latLngLocation) {
        this.latLngLocation = latLngLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
