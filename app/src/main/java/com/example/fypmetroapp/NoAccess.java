package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NoAccess extends AppCompatActivity {

    final static Fragment maps_noloc = new Maps_No_Location_Access();
    final static Fragment profileFragment = new ProfileFragment();
    //final Fragment userprefs = new UserPreferencesFragment();
    final static Fragment ticketFragment = new TicketFragment();
    final static Fragment homeFragment_noLocation = new NoLocationAccess();
    static FragmentManager fm;
    static Fragment active;
    private DrawerLayout dl;
    private NavigationView nv;
    ImageButton showButton, hideButton, legendButton;
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseAuth firebaseAuth;
    String name, uid, role;
    public static TextView navname, navid;
    SharedPreferences preferences;
    public static Activity activity;
    static BottomNavigationView navigation;
    String provider;
    LocationManager locationManager;
    static BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_access);
        fm = this.getSupportFragmentManager();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        activity = this;

        //getSupportActionBar().hide();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set launch activity
        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        role = preferences.getString("role", null);

        //buggy switching from launch fix
        //if location off
        fm.beginTransaction().add(R.id.main_container_noacess, maps_noloc, "1").hide(maps_noloc).commit();
        fm.beginTransaction().add(R.id.main_container_noacess, homeFragment_noLocation, "1").commit();
        //fm.beginTransaction().add(R.id.main_container, userprefs, "6").hide(userprefs).commit();
        fm.beginTransaction().add(R.id.main_container_noacess, ticketFragment, "4").hide(ticketFragment).commit();
        fm.beginTransaction().add(R.id.main_container_noacess, profileFragment, "5").hide(profileFragment).commit();
        fm.executePendingTransactions();
        active = homeFragment_noLocation;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.drawerLayout);
        legendButton = findViewById(R.id.legend_show);
        legendButton.setOnClickListener(v -> NoLocationAccess.stationLegendReminder.show());
        navname = findViewById(R.id.nameToolbar);
        navid = findViewById(R.id.IDToolbar);
        nv = findViewById(R.id.left_menu);

        showButton = toolbar.findViewById(R.id.menu_show);
        hideButton = nv.findViewById(R.id.menu_hide);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        nv.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.account:
                    navigation.setSelectedItemId(R.id.navigation_profile);
                    fm.beginTransaction()
                            .hide(active)
                            .show(profileFragment)
                            .commit();
                    active = profileFragment;
                    dl.closeDrawers();
                    return true;
                /*case R.id.settings:
                    fm.beginTransaction()
                            .hide(active_map)
                            .show(userprefs)
                            .commit();
                    active_map = userprefs;
                    dl.closeDrawers();
                    return true;*/
                case R.id.logout:
                    LogOutUser();
                    Toast.makeText(NoAccess.this, "Signing Out...",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
        showButton.setOnClickListener(Drawer_Menu_Buttons);
        hideButton.setOnClickListener(Drawer_Menu_Buttons);
        getuser();

        navigation = findViewById(R.id.navigation);
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_nearMe:
                        fm.beginTransaction()
                                .hide(active)
                                .show(maps_noloc)
                                .commit();
                        active = maps_noloc;
                        return true;

                    case R.id.navigation_profile:
                        fm.beginTransaction()
                                .hide(active)
                                .show(profileFragment)
                                .commit();
                        active = profileFragment;
                        return true;


                    case R.id.navigation_home:
                        fm.beginTransaction()
                                .hide(active)
                                .show(homeFragment_noLocation)
                                .commit();
                        active = homeFragment_noLocation;
                        return true;
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        super.onStart();
    }

    //event listener for buttons
    android.view.View.OnClickListener Drawer_Menu_Buttons = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.menu_show:
                    dl.openDrawer(Gravity.LEFT);
                    break;
                case R.id.menu_hide:
                    dl.closeDrawers();
                    break;
            }
        }
    };

    public void getuser () {
        new Handler().postDelayed(() -> {
            uid = firebaseAuth.getCurrentUser().getUid();
            //Log.e("user", uid);

            name = preferences.getString("full_name", null);
            uid = preferences.getString("user_id", null);
            role = preferences.getString("role", null);

            navid.setText(uid);
            navname.setText(name);
        }, 100);
    }

    @SuppressLint("NewApi")
    public void LogOutUser () {
        SharedPreferences preferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent logOutIntent = new Intent(this, MainActivity.class);
        startActivity(logOutIntent);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawers();
        } else {
            moveTaskToBack(false);
            super.onBackPressed();
        }
    }
}