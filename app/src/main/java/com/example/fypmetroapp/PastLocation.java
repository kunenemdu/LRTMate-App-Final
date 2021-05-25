package com.example.fypmetroapp;

import androidx.lifecycle.Lifecycle;

import com.google.android.gms.maps.model.LatLng;

public class PastLocation {
    private String name;
    private LatLng location;
    private Lifecycle.State state;

    public enum State {
        FAVOURITE,
        WORK,
        NORMAL,
        HOME
    }

    public Lifecycle.State getState() {
        return state;
    }

    public void setState(Lifecycle.State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
