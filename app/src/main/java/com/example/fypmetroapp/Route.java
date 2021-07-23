package com.example.fypmetroapp;

import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Route {
    private ArrayList<String> instructions, summary;
    private int total_route_duration, cost, stops_duration, total_route_length;
    private ArrayList<Bus> buses;
    private ArrayList<String> ridingList;
    private ArrayList<Station> ridingStations;
    Station origin, destination, current, next;
    DirectionsResult origin_walking, destination_walking;
    Map<String, Map> main = new HashMap<String, Map>();
    LatLng journey_origin, journey_destination;

    public LatLng getJourney_origin() {
        return journey_origin;
    }

    public void setJourney_origin(LatLng journey_origin) {
        this.journey_origin = journey_origin;
    }

    public LatLng getJourney_destination() {
        return journey_destination;
    }

    public void setJourney_destination(LatLng journey_destination) {
        this.journey_destination = journey_destination;
    }

    public Map<String, Map> getMain() {
        return main;
    }

    public void setMain(Map<String, Map> main) {
        this.main = main;
    }

    public int getTotal_route_length() {
        return total_route_length;
    }

    public void setTotal_route_length(int total_route_length) {
        this.total_route_length = total_route_length;
    }

    public DirectionsResult getOrigin_walking() {
        return origin_walking;
    }

    public void setOrigin_walking(DirectionsResult origin_walking) {
        this.origin_walking = origin_walking;
    }

    public DirectionsResult getDestination_walking() {
        return destination_walking;
    }

    public void setDestination_walking(DirectionsResult destination_walking) {
        this.destination_walking = destination_walking;
    }

    public Station getOrigin() {
        return origin;
    }

    public void setOrigin(Station origin) {
        this.origin = origin;
    }

    public Station getDestination() {
        return destination;
    }

    public void setDestination(Station destination) {
        this.destination = destination;
    }

    public ArrayList<Station> getRidingStations() {
        return ridingStations;
    }

    public void setRidingStations(ArrayList<Station> ridingStations) {
        this.ridingStations = ridingStations;
    }

    public Station getCurrent() {
        return current;
    }

    public void setCurrent(Station current) {
        this.current = current;
    }

    public Station getNext() {
        return next;
    }

    public void setNext(Station next) {
        this.next = next;
    }

    public ArrayList<String> getRidingList() {
        return ridingList;
    }

    public void setRidingList(ArrayList<String> ridingList) {
        this.ridingList = ridingList;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public ArrayList<String> getSummary() {
        return summary;
    }

    public void setSummary(ArrayList<String> summary) {
        this.summary = summary;
    }

    public int getTotal_route_duration() {
        return total_route_duration;
    }

    public void setTotal_route_duration(int total_route_duration) {
        this.total_route_duration = total_route_duration;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getStops_duration() {
        return stops_duration;
    }

    public void setStops_duration(int stops_duration) {
        this.stops_duration = stops_duration;
    }

    public ArrayList<Bus> getBuses() {
        return buses;
    }

    public void setBuses(ArrayList<Bus> buses) {
        this.buses = buses;
    }
}
