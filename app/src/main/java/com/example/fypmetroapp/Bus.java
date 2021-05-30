package com.example.fypmetroapp;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bus {
    int name;
    ArrayList<Station> stops;
    Polyline busPolyline;

    public Polyline getBusPolyline() {
        return busPolyline;
    }

    public void setBusPolyline(Polyline busPolyline) {
        this.busPolyline = busPolyline;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public ArrayList<Station> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Station> stops) {
        this.stops = stops;
    }

    //show bus route line
    public void busRoute (Bus bus) {
        GoogleMap googleMap = Maps_Full_Access.gMap;
        ArrayList<Polyline> route = Maps_Full_Access.route;
        ArrayList<Station> busStops = bus.getStops();
        Station previous;

        for (int i = 0; i < busStops.size(); i++) {
            previous = busStops.get(i);
            int next = i + 1;

            if (next < busStops.size()) {
                Station station = busStops.get(next);

                String origin = String.valueOf(previous.position.latitude) + "," + String.valueOf(previous.position.longitude);
                String destination = String.valueOf(station.position.latitude) + "," + String.valueOf(station.position.longitude);

                Log.e(previous.name, origin);
                Log.e(station.name, destination);

                //manipulate directionsAPI
                DirectionsResult results = getDirectionsDetails(origin, destination, TravelMode.DRIVING);
                if (results != null) {
                    if (googleMap != null) {
                        route.add(addBusPolyline(results, googleMap));
                        positionCamera(results.routes[0], googleMap);
                    }else
                        Log.e("maps ", "null");
                }
            }
        }
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[0].startLocation.lat, route.legs[0].startLocation.lng), 8));
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(new Maps_Full_Access().getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Polyline addBusPolyline(DirectionsResult results, GoogleMap mMap) {
        // Draw a dashed (60px spaced) blue polyline
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        busPolyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        busPolyline.setColor(Color.BLUE);
        busPolyline.setWidth(20f);
        setBusPolyline(busPolyline);

        return busPolyline;
    }

    private void togglePolyline () {
        if (busPolyline.isVisible()) {
            busPolyline.setVisible(false);
        }
    }
}
