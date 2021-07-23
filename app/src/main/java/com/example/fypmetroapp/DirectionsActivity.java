package com.example.fypmetroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class DirectionsActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior_NearBy;
    TextView titleText, textAddress;
    GoogleMap gMap;
    private static final int overview = 0;
    Marker marker;
    CardView drivingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gMap = MapsFragmentExtras.gMap;

        if (gMap != null) {
            bottomSheetBehavior_NearBy = new Maps_No_Location_Access().bottomSheetBehavior_NearBy;
            titleText = new Maps_No_Location_Access().titleText;
            textAddress = new Maps_No_Location_Access().textAddress;
        }

        Log.e("used ", "directions");
    }
}