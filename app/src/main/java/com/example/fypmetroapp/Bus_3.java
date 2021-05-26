package com.example.fypmetroapp;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bus_3 extends Bus {
    @Override
    public int getName() {
        return 3;
    }

    @Override
    public ArrayList<Station> getStops() {
        ArrayList<Station> busStops = new ArrayList<>();
        busStops.add(new QB().addStation());
        busStops.add(new Kennedy().addStation());
        busStops.add(new BeauBassin().addStation());
        busStops.add(new St_John().addStation());
        busStops.add(new Victoria().addStation());

        return busStops;
    }

    @Override
    public void setName(int name) {
        super.setName(3);
    }
}
