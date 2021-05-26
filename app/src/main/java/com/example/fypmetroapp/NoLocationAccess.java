package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static android.graphics.Color.TRANSPARENT;

public class NoLocationAccess extends Fragment {

    Button grant, setLocation;
    public static Dialog stationLegendReminder;
    SharedPreferences preferences;
    static FragmentManager fm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        fm = NoAccess.fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_location_access, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        grant = getView().findViewById(R.id.grantPerms);
        setLocation = getView().findViewById(R.id.setLocation);
        grant.setOnClickListener(buttons);
        setLocation.setOnClickListener(buttons);
        stationLegendReminder = new Dialog(getActivity());

        Handler legend = new Handler();
        legend.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean legend_seen = preferences.getBoolean("legend_seen", false);
                    if (legend_seen == false)
                        showStationsLegend(true);
                    else
                        showStationsLegend(false);
            }
        }, 5000);
    }

    @SuppressLint("NewApi")
    private void showStationsLegend (boolean show) {
        stationLegendReminder.setContentView(R.layout.station_legend_reminder);
        stationLegendReminder.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        ImageButton closeDialog = stationLegendReminder.findViewById(R.id.dialog_closeX);

        if (show == true) {
            stationLegendReminder.show();
        }

        closeDialog.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("legend_seen", true);
            editor.apply();
            stationLegendReminder.dismiss();
        });
    }

    View.OnClickListener buttons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.grantPerms:
                    Log.e("clicked", "perms");
                    Dexter.withActivity(getActivity())
                            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    startActivity(new Intent(getContext(), Maps_No_Location_Access.class));
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    if (response.isPermanentlyDenied()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Permission Missing!")
                                                .setMessage("Permission to access to your location denied. Location access is required!")
                                                .setNegativeButton("Cancel", null)
                                                .setPositiveButton("Allow", (dialog, which) -> {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                                                })
                                                .show();
                                    }
                                }
                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            });
                    break;
                case R.id.setLocation:
                    Log.e("clicked", "set");
                    Fragment maps_noloc = new Maps_No_Location_Access();
                    fm.beginTransaction()
                            .hide(NoAccess.active)
                            .show(maps_noloc)
                            .commit();
                    NoAccess.active = maps_noloc;
                    NoAccess.navigation.setSelectedItemId(R.id.navigation_nearMe);
                    break;
            }
        }
    };
}