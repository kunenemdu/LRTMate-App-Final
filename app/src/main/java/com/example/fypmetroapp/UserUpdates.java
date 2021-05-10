package com.example.fypmetroapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;

public class UserUpdates {
    public static Location location;
    public static Location new_location;
    public static LatLng latLngLocation;
    public static boolean updatedOcc;
    public static LatLng latLngDestination;
    public static boolean tracking;
    public static int distance_to_nearest_station;
    public static Station nearest_station;
    public static ArrayList<Station> _proximity_stations;
    public static double cur_stat_occupancy;
    public String cur_stat_next;
    public String cur_stat_after;
    public String user_time;
    public int arrives_in;
    public static ArrayList<StationFence> foundFences;

    public static ArrayList<StationFence> getFoundFences() {
        return foundFences;
    }

    public static void setFoundFences(ArrayList<StationFence> foundFences) {
        UserUpdates.foundFences = foundFences;
    }

    public static Location getNew_location() {
        return new_location;
    }

    public static void setNew_location(Location new_location) {
        UserUpdates.new_location = new_location;
    }

    public int getArrives_in() {
        return arrives_in;
    }

    public void setArrives_in(int arrives_in) {
        this.arrives_in = arrives_in;
    }

    public String getUser_time() {
        return user_time;
    }

    public void setUser_time(String user_time) {
        this.user_time = user_time;
    }

    public void setUpdatedOcc(boolean updatedOcc) {
        UserUpdates.updatedOcc = updatedOcc;
    }

    public boolean isUpdatedOcc() {
        return updatedOcc;
    }

    public String getCur_stat_next() {
        return cur_stat_next;
    }

    public void setCur_stat_next(String cur_stat_next) {
        this.cur_stat_next = cur_stat_next;
    }

    public String getCur_stat_after() {
        return cur_stat_after;
    }

    public void setCur_stat_after(String cur_stat_after) {
        this.cur_stat_after = cur_stat_after;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public double getCur_stat_occupancy() {
        return cur_stat_occupancy;
    }

    public void setCur_stat_occupancy(double cur_stat_occupancy) {
        this.cur_stat_occupancy = cur_stat_occupancy;
    }

    public ArrayList<Station> get_proximity_stations() {
        return _proximity_stations;
    }

    public void set_proximity_stations(ArrayList<Station> _proximity_stations) {
        this._proximity_stations = _proximity_stations;
    }

    public Station getNearest_station() {
        return nearest_station;
    }

    public void setNearest_station(Station nearest_station) {
        this.nearest_station = nearest_station;
    }

    public int getDistance_to_nearest_station() {
        return distance_to_nearest_station;
    }

    public void setDistance_to_nearest_station(int distance_to_nearest_station) {
        this.distance_to_nearest_station = distance_to_nearest_station;
    }

    public LatLng getLatLngDestination() {
        return latLngDestination;
    }

    public void setLatLngDestination(LatLng latLngDestination) {
        this.latLngDestination = latLngDestination;
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
