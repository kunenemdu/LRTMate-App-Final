package com.example.fypmetroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

public class PermissionsActivity extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private boolean permissionLocation = false;
    private boolean permissionDenied = false;
    int perms;
    String provider;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        //getSupportActionBar().hide();
        perms = 0;

        //if (ContextCompat.checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
        if (isLocationEnabled(this) == true) {
            Log.e("is", "on");
            startSplashActivity();
        }
        else if (isLocationEnabled(this) == false) {
            Log.e("not", "on");
            requestLocation();
        }
    }

    public boolean isLocationEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && LocationManagerCompat.isLocationEnabled(manager);
    }

    public int getPerms() {
        return perms;
    }

    public void setPerms(int perms) {
        this.perms = perms;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void requestLocation () {
        if (isLocationEnabled(getApplicationContext()) == true) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                showMissingPermissionError();
                checkLocationPermission();
                setPerms(0);
            }
        }
        else {
            Log.e("disabled", "loc");
            setPerms(1);
            startOtherActivity();
        }
    }

    public void checkLocationPermission () {
        //ask for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setPerms(1);
        } else {
            //prompt user to allow location permission
            showMissingPermissionError();
            setPerms(0);
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            startSplashActivity();
        }
        else {
            showMissingPermissionError();
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    private void startSplashActivity() {
        Intent loginIntent = new Intent(PermissionsActivity.this, NavigationActivity.class);
        startActivity(loginIntent);
    }

    private void startOtherActivity() {
        Intent loginIntent = new Intent(PermissionsActivity.this, NoAccess.class);
        startActivity(loginIntent);
    }

    public void checkSMSPermission () {
        //ask for sms permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            showMissingPermissionError();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            startSplashActivity();
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    //TODO: DIALOG FOR ALLOWING LOCATION PERMISSION
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog denied = new PermissionUtils.PermissionDeniedDialog();
        denied.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.e("not enabled", location.toString());
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.e("enabled", "provider");
        if (getPerms() == 0) {
            requestLocation();
        }
        else if (getPerms() == 1)
            startSplashActivity();
        //requestLocation();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.e("not enabled", "provider");
        checkLocationPermission();
    }
}