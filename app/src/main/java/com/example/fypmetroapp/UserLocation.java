package com.example.fypmetroapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    Location location;
    LatLng latLngLoc;

    public LatLng getLatLngLoc() {
        return latLngLoc;
    }

    public void setLatLngLoc(LatLng latLngLoc) {
        this.latLngLoc = latLngLoc;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
