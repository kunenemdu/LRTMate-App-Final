package com.example.fypmetroapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;

public class PermissionsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private boolean permissionLocation = false;
    private boolean permissionDenied = false;

    int perms = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        //getSupportActionBar().hide();

        requestLocation();
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
        if (getPerms() == 0) {
            requestLocation();
        }
        else if (getPerms() == 1)
            startSplashActivity();
    }

    private void requestLocation () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            setPerms(0);
            showMissingPermissionError();
            checkLocationPermission();
        }
        else {
            setPerms(1);
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

            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            startSplashActivity();
        } else {
            // Permission was denied. Display an error message
            showMissingPermissionError();
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    private void startSplashActivity() {
        Intent loginIntent = new Intent(PermissionsActivity.this, MainActivity.class);
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
}