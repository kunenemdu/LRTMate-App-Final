package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.graphics.Color.*;

public class NavigationActivity extends AppCompatActivity {

    final Fragment mapsNewer = new MapsNewer();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment userprefs = new UserPreferencesFragment();
    final Fragment ticketFragment = new TicketFragment();
    final Fragment homeFragment = new HomeFragment();
    static FragmentManager fm;
    Fragment active;
    private DrawerLayout dl;
    private NavigationView nv;
    ImageButton showButton, hideButton, legendButton;
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseAuth firebaseAuth;
    String name, uid;
    public static TextView navname, navid;
    SharedPreferences preferences;
    public static Activity activity;
    Dialog stationLegendReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        fm = this.getSupportFragmentManager();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        activity = this;

        //getSupportActionBar().hide();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set launch activity
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, homeFragment, "1").commit();
        fm.beginTransaction().add(R.id.main_container, mapsNewer, "2").hide(mapsNewer).commit();
        fm.beginTransaction().add(R.id.main_container, userprefs, "5").hide(userprefs).commit();
        fm.beginTransaction().add(R.id.main_container, ticketFragment, "3").hide(ticketFragment).commit();
        fm.beginTransaction().add(R.id.main_container, profileFragment, "4").hide(profileFragment).commit();
        fm.executePendingTransactions();
        //buggy switching from launch
        active = homeFragment;
        preferences = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        stationLegendReminder = new Dialog(this);
        showStationsLegend();
    }

    private void showStationsLegend () {
        stationLegendReminder.setContentView(R.layout.station_legend_reminder);
        stationLegendReminder.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        ImageButton closeDialog = stationLegendReminder.findViewById(R.id.dialog_closeX);
        stationLegendReminder.show();

        closeDialog.setOnClickListener(v -> stationLegendReminder.dismiss());
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        super.onStart();
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.drawerLayout);
        showButton = findViewById(R.id.bt_menu_show);
        legendButton = findViewById(R.id.legend_show);
        legendButton.setOnClickListener(v -> showStationsLegend());
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
                    fm.beginTransaction()
                            .hide(active)
                            .show(profileFragment)
                            .commit();
                    active = profileFragment;
                    dl.closeDrawers();
                return true;
                case R.id.settings:
                    fm.beginTransaction()
                            .hide(active)
                            .show(userprefs)
                            .commit();
                    active = userprefs;
                    dl.closeDrawers();
                return true;
                case R.id.logout:
                    LogOutUser();
                    Toast.makeText(NavigationActivity.this, "Signing Out...",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
        showButton.setOnClickListener(Drawer_Menu_Buttons);
        hideButton.setOnClickListener(Drawer_Menu_Buttons);
        getuser();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uid = firebaseAuth.getCurrentUser().getUid();
                //Log.e("user", userid);

                DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
                documentReference.addSnapshotListener(NavigationActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (documentSnapshot != null) {
                            if (documentSnapshot.exists() == true) {
                                //show users full name
                                name = documentSnapshot.getString("full_name");

                                navname.setText(name);
                                navid.setText(uid);
                            }
                        }
                    }
                });
            }
        }, 1000);
    }

    @SuppressLint("NewApi")
    public void LogOutUser () {
        SharedPreferences preferences = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        preferences = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent logOutIntent = new Intent(this, MainActivity.class);
        startActivity(logOutIntent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_nearMe:
                    fm.beginTransaction()
                            .hide(active)
                            .show(mapsNewer)
                            .commit();
                    active = mapsNewer;
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction()
                            .hide(active)
                            .show(profileFragment)
                            .commit();
                    active = profileFragment;
                    return true;

                case R.id.navigation_ticket:
                    fm.beginTransaction()
                            .hide(active)
                            .show(ticketFragment)
                            .commit();
                    active = ticketFragment;
                    return true;

                case R.id.navigation_home:
                    fm.beginTransaction()
                            .hide(active)
                            .show(homeFragment)
                            .commit();
                    active = homeFragment;
                    return true;
            }
            return false;
        }
    };
}