package com.example.fypmetroapp;

import java.util.ArrayList;

public class Route {
    private ArrayList<String> instructions, summary;
    private int total_route_duration, cost, stops_duration;
    private ArrayList<Bus> buses;
    private ArrayList<String> ridingList;
    Station origin, destination;

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
