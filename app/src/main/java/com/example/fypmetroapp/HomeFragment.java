package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //TextView locText;
    Location mLocation;
    LatLng mLocLatLng;
    GoogleMap gMap;
    GlobalProperties properties = new GlobalProperties();
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference userRef, stationRef;
    LocationCallback locationCallback;
    GeoFire userGeoFire, stationGeoFire;
    Boolean requestingLocationUpdates = false;
    static TextView status, proximity, stationText, occupancy, statType, nextArrival;
    String user_id;
    static Button begin, stop;
    static TickerView tickerView;
    UserUpdates userUpdates;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        userUpdates = new UserUpdates();
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //locText = getView().findViewById(R.id.locText);

        //on load pan camera to user's location
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
        occupancy = getView().findViewById(R.id.occupancy);
        statType = getView().findViewById(R.id.stationType);
        //nextArrival = getView().findViewById(R.id.nextArrival);
        status = getView().findViewById(R.id.currentStatus);
        stationText = getView().findViewById(R.id.curStation);
        proximity = getView().findViewById(R.id.proximity);
        begin = getView().findViewById(R.id.begin_btn);
        stop = getView().findViewById(R.id.stop_btn);

        tickerView = getView().findViewById(R.id.nextArrival);
        tickerView.setCharacterLists(TickerUtils.provideNumberList());

        proximity.setText("Waiting to");
        stationText.setText("Receive Updates...");
        status.setText("Waiting to Receive Updates...");
        occupancy.setText("Waiting to Receive Updates...");
        statType.setText("Waiting to");
        tickerView.setText("Receive Updates...");

        statType.setTextColor(Color.BLACK);
        //nextArrival.setTextColor(Color.BLACK);
        status.setTextColor(Color.BLACK);
        occupancy.setTextColor(Color.BLACK);
        proximity.setTextColor(Color.BLACK);
        stationText.setTextColor(Color.BLACK);

        begin.setOnClickListener(track_buttons);
        stop.setOnClickListener(track_buttons);

        mLocation = manager.getLastKnownLocation(bestProvider);
        if (mLocation != null) {
            userUpdates.location = mLocation;
            userUpdates.latLngLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //LatLng userLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            //String userLocation = getAddress(getContext(), mLocation.getLatitude(), mLocation.getLongitude());
            //locText.setText(userLocation);
        }

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("UserPrefs", Config.MODE_PRIVATE);
        String role = pref.getString("role", null);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (role.equals("User")) {
                            buildLocationRequest();
                            buildLocationCallback();
                        }
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.home_map_frags);
                        mapFragment.getMapAsync(HomeFragment.this::onMapReady);
                        GeoFireConfig();
                        GeoFireConfigStations();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getContext(), "You have to enable Location Access!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    View.OnClickListener track_buttons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.begin_btn:
                    userUpdates.tracking = true;
                    initTracking();
                    break;
                case R.id.stop_btn:
                    userUpdates.tracking = false;
                    stopTracking();
                    break;
            }
        }
    };

    private void stopTracking () {
        begin.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        if (userUpdates.tracking == false) {
            proximity.setText("Waiting to");
            stationText.setText("Receive Updates...");
            status.setText("Waiting to Receive Updates...");
            occupancy.setText("Waiting to Receive Updates...");
            status.setTextColor(Color.BLACK);
            occupancy.setTextColor(Color.BLACK);
            proximity.setTextColor(Color.BLACK);
            stationText.setTextColor(Color.BLACK);
        }
    }

    public static void initOccupancy () {
        if (UserUpdates.tracking == true) {
            if (UserUpdates.nearest_station != null) {
                if (UserUpdates.cur_stat_occupancy != 0) {
                    if (UserUpdates.cur_stat_occupancy >= 3.0) {
                        occupancy.setText("Very Active");
                        occupancy.setTextColor(Color.RED);
                    }
                    else if (UserUpdates.cur_stat_occupancy >= 2.0) {
                        occupancy.setText("Active");
                        int orange = Color.rgb(255, 165, 0);
                        occupancy.setTextColor(orange);
                    }
                    else {
                        occupancy.setText("Quiet");
                        occupancy.setTextColor(Color.GREEN);
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void initTracking() {
        begin.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);
        //Log.e("loc", UserUpdates.nearest_station.name);
        //getCount(userUpdates.getNearest_station().name);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);
                if (userUpdates.tracking == true) {
                    if (userUpdates.nearest_station != null) {
                        if (userUpdates.cur_stat_occupancy != 0) {
                            //time = d/s [avg walking speed humans = 6kph]
                            //TODO: CALCULATE THIS USING USER'S AVG SPEED IF ON FOOT/DRIVING/CYCLING
                            int time = userUpdates.distance_to_nearest_station / 6;
                            proximity.setText("~ " + time + " min(s) from");
                            proximity.setTextColor(Color.GREEN);
                            stationText.setText(userUpdates.nearest_station.name);
                            stationText.setTextColor(Color.BLUE);

                            if (userUpdates.cur_stat_occupancy >= 3.0) {
                                occupancy.setText("Very Active");
                                occupancy.setTextColor(Color.RED);
                            }
                            else if (userUpdates.cur_stat_occupancy >= 2.0) {
                                occupancy.setText("Active");
                                int orange = Color.rgb(255, 165, 0);
                                occupancy.setTextColor(orange);
                            }
                            else {
                                occupancy.setText("Quiet");
                                occupancy.setTextColor(Color.GREEN);
                            }

                            try {
                                Time current_time = new Time(Time.getCurrentTimezone());
                                current_time.setToNow();
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                Date date1 = format.parse(current_time.format("%k:%M"));
                                String next = userUpdates.getCur_stat_next();
                                Date date2 = format.parse(next);
                                long difference = date2.getTime() - date1.getTime();
                                int arrives_in = (int) (difference / 60000);
                                Log.e("next arrives in", String.valueOf(arrives_in));
                                statType.setText(userUpdates.getNearest_station().type);
                                tickerView.setText(arrives_in + " minute(s)");
                                //Log.e("next", userUpdates.getCur_stat_next());
                                //Log.e("time", String.valueOf(current_time.format("%k:%M")));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, 5000);
    }

    private Task<Integer> getCount(String stationName) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firebaseFirestore
                .collection("stationdetails")
                .document("arrivals")
                .collection(userUpdates.nearest_station.type)
                .document(stationName);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                userUpdates.cur_stat_next = (snapshot.getString("next"));
                userUpdates.cur_stat_after = (snapshot.getString("after"));
            }
        });
        return null;
    }

    public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        String userLoc = "";
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


                userLoc = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String address = addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userLoc;
    }

    private void GeoFireConfig() {
        userRef = FirebaseDatabase.getInstance().getReference("lrtmateapp");
        userGeoFire = new GeoFire(userRef);
    }

    private void GeoFireConfigStations() {
        stationRef = FirebaseDatabase.getInstance().getReference("StationFences").child("Stations");
        stationGeoFire = new GeoFire(stationRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                gMap = properties.getgMap();
                if (gMap != null) {
                    if (user_id != null) {
                        userGeoFire.setLocation(user_id, new GeoLocation(
                                locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude()));

                        startLocationUpdates();
                        LatLng currentLatLngLocation = userUpdates.latLngLocation;
                        Location currentLocation = locationResult.getLastLocation();
/*
                        Log.e("home loc", currentLocation.toString());
                        Log.e("home loc", currentLatLngLocation.toString());*/
                        //Log.e("previous distance", String.valueOf(pre_total_distance));
                    } else {
                        Log.e("no", "id");
                    }
                } else {
                    Log.e("maps", "failed");
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(2500);
        locationRequest.setSmallestDisplacement(25f);
    }

    @Override
    public void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @SuppressLint({"ResourceType", "NewApi"})
    public void onMapReady(GoogleMap googleMap) {
        //initialise map for use
        gMap = googleMap;
        properties.setgMap(gMap);

        gMap = Tools.configActivityMaps(googleMap);

        //call location before doing anything with it
        enableMyLocation();

        LatLngBounds mauritius = new LatLngBounds(
                new LatLng(-20.523707, 57.277314),
                new LatLng(-20.000119, 57.847562)
        );

        //initialise ImageView
        View locationButton = getView().findViewById(0x2);
        View searchButton = getView().findViewById(R.id.places_autocomplete_search_button);

        // Change the visibility of my location button
        if (locationButton != null)
            locationButton.setVisibility(View.GONE);

        // Change the visibility of my location button
        if (searchButton != null)
            searchButton.setVisibility(View.GONE);

        gMap.setOnMapLoadedCallback(() -> {

            //gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mauritius, 30));

            gMap.setLatLngBoundsForCameraTarget(mauritius);

        });

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);

                //on load pan camera to user's location
                LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria mCriteria = new Criteria();
                String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
                userUpdates.location = (manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
                if (userUpdates.location != null) {

                    final double currentLatitude = userUpdates.location.getLatitude();
                    final double currentLongitude = userUpdates.location.getLongitude();
                    LatLng loc1 = new LatLng(currentLatitude, currentLongitude);

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 16));
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                }
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(((AppCompatActivity) getContext()), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }
}