package com.example.fypmetroapp;

import java.util.ArrayList;

public class PreviousJourneys {
    ArrayList<String> previous_summary;
    ArrayList<String> previous_full;

    public ArrayList<String> getPrevious_full() {
        return previous_full;
    }

    public void setPrevious_full(ArrayList<String> previous_full) {
        this.previous_full = previous_full;
    }

    public ArrayList<String> getPrevious_summary() {
        return previous_summary;
    }

    public void setPrevious_summary(ArrayList<String> previous_summary) {
        this.previous_summary = previous_summary;
    }
}
