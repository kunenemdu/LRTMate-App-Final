package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
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
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NavigationActivity extends AppCompatActivity {

    final Fragment maps_holder = new NearMeHolder();
    final Fragment profileFragment = new ProfileFragment();
    final Fragment userprefs = new UserPreferencesFragment();
    final Fragment ticketFragment = new TicketFragment();
    final Fragment homeFragment_user = new HomeFragment_User();
    final Fragment homeFragment_driver = new HomeFragment_Driver();
    static FragmentManager fm;
    static Fragment active;
    private DrawerLayout dl;
    private NavigationView nv;
    static ImageButton showButton, hideButton, legendButton, show_tips;
    public static FirebaseFirestore firebaseFirestore;
    public static FirebaseAuth firebaseAuth;
    String name, uid, role;
    public static TextView navname, navid;
    SharedPreferences preferences;
    public static Activity activity;
    static BottomNavigationView navigation;
    String provider;
    LocationManager locationManager;
    static androidx.appcompat.widget.Toolbar toolbar;
    public static TinyDB tinyDB;


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
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        //role = preferences.getString("role", null);
        tinyDB = new TinyDB(getApplicationContext());
        role = tinyDB.getString("role");

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        //buggy switching from launch fix
        fm.beginTransaction().add(R.id.main_container, maps_holder, "1").hide(maps_holder).commit();
        if (role.equals("User")) {
            fm.beginTransaction().add(R.id.main_container, homeFragment_user, "2").commit();
            active = homeFragment_user;
        } else if (role.equals("Driver")) {
            fm.beginTransaction().add(R.id.main_container, homeFragment_driver, "3").commit();
            active = homeFragment_driver;
        }
        fm.beginTransaction().add(R.id.main_container, userprefs, "6").hide(userprefs).commit();
        fm.beginTransaction().add(R.id.main_container, ticketFragment, "4").hide(ticketFragment).commit();
        fm.beginTransaction().add(R.id.main_container, profileFragment, "5").hide(profileFragment).commit();
        fm.executePendingTransactions();
    }


    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        NavigationActivity.toolbar = toolbar;
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.drawerLayout);
        legendButton = findViewById(R.id.legend_show);
        legendButton.setOnClickListener(v -> HomeFragment_User.stationLegendReminder.show());
        navname = findViewById(R.id.nameToolbar);
        navid = findViewById(R.id.IDToolbar);
        nv = findViewById(R.id.left_menu);

        showButton = toolbar.findViewById(R.id.menu_show);
        hideButton = nv.findViewById(R.id.menu_hide);
        show_tips = toolbar.findViewById(R.id.show_tips);
        if (role.equals("User")) {
            show_tips.setOnClickListener(v -> HomeFragment_User.showToolTipsHome());
        }

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
                case R.id.show_tips:
                    HomeFragment_User.showToolTipsHome();
                    break;
            }
        }
    };

    public void getuser () {
        new Handler().postDelayed(() -> {
            uid = firebaseAuth.getCurrentUser().getUid();
            //Log.e("user", uid);
            TinyDB tinyDB = new TinyDB(getApplicationContext());
            User user = tinyDB.getObject("User", User.class);

            navid.setText(user.getUserid());
            navname.setText(user.getFullname());
        }, 100);
    }

    @SuppressLint("NewApi")
    public void LogOutUser () {
        FirebaseAuth.getInstance().signOut();
        Intent logOutIntent = new Intent(this, MainActivity.class);
        startActivity(logOutIntent);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_nearMe:
                    fm.beginTransaction()
                            .hide(active)
                            .show(maps_holder)
                            .commit();
                    active = maps_holder;
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
                            .show(homeFragment_user)
                            .commit();
                    active = homeFragment_user;
                    return true;
            }
            return false;
        }
    };

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

    static void displayToolTip(int position, int align, View view) {
        String message = "";
        ToolTipsManager manager = HomeFragment_User.manager;
        switch (view.getId()) {
            case R.id.legend_show:
            case R.id.show_tips:
                message = "Want To See These Tips Again?";
                break;
        }
        ToolTip.Builder builder = new ToolTip.Builder(view.getContext(), view, toolbar, message, position);
        builder.setAlign(align);
        builder.setBackgroundColor(Color.BLUE);
        manager.show(builder.build());
    }
}