package com.example.fypmetroapp;

import java.util.Comparator;

public class SortByDistance implements Comparator<Station> {
    public int compare (Station a, Station b) {
        return a.distance - b.distance;
    }
}
