package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFire;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.TRANSPARENT;

public class HomeFragment_Driver extends Fragment implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //TextView locText;
    Location mLocation;
    GoogleMap gMap;
    GlobalProperties properties = new GlobalProperties();
    DatabaseReference stationRef;
    GeoFire userGeoFire, stationGeoFire;
    static TextView status, proximity, occupancy, statType, init;
    String user_id;
    static Button begin, stop;
    static TickerView next_arrivalTicker, cur_stationTicker, progress_ticker;
    public static Dialog stationLegendReminder;
    String role;
    ProgressBar loaderBar;
    LinearLayout loader, main;
    private int progressStatus = 0;
    private Handler progress_handler = new Handler();
    private LocationManager locationManager;
    private String provider;
    Dialog buses_at_station;
    TextView txtBusName;
    boolean selected_bus = false;
    final Handler timeHandler = new Handler(Looper.getMainLooper());
    LinearLayout indic;
    private DatabaseReference mDatabase;
    static Bus_DriverUpdates updates = new Bus_DriverUpdates();
    Station station = new QB().addStation();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_driver, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderBar = (ProgressBar) getView().findViewById(R.id.loadBar);
        loaderBar.setMax(100); //15000ms is 15s
        loader = getView().findViewById(R.id.loaderLL);
        main = getView().findViewById(R.id.content_home);
        progress_ticker = getView().findViewById(R.id.progress_ticker);
        progress_ticker.setCharacterLists(TickerUtils.provideNumberList());
        buses_at_station = new Dialog(getActivity());

        //on load pan camera to user's location
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
        occupancy = getView().findViewById(R.id.occupancy);
        statType = getView().findViewById(R.id.stationType);
        status = getView().findViewById(R.id.currentStatus);
        proximity = getView().findViewById(R.id.proximity);
        begin = getView().findViewById(R.id.begin_btn);
        stop = getView().findViewById(R.id.stop_btn);
        init = getView().findViewById(R.id.init);
        indic = getView().findViewById(R.id.ringIndic);

        cur_stationTicker = getView().findViewById(R.id.curStation);
        cur_stationTicker.setCharacterLists(TickerUtils.provideAlphabeticalList());
        next_arrivalTicker = getView().findViewById(R.id.nextArrival);
        next_arrivalTicker.setCharacterLists(TickerUtils.provideNumberList());

        proximity.setText("Waiting to");
        cur_stationTicker.setText("Receive Updates...");
        status.setText("Waiting to Receive Updates...");
        occupancy.setText("Waiting to Receive Updates...");
        statType.setText("Waiting to");
        next_arrivalTicker.setText("Receive Updates...");

        status.setTextColor(Color.BLACK);
        occupancy.setTextColor(Color.BLACK);
        proximity.setTextColor(Color.BLACK);
        cur_stationTicker.setTextColor(Color.BLUE);

        begin.setOnClickListener(track_buttons);
        stop.setOnClickListener(track_buttons);

        //Log.e("prefs are", preferences.getAll().toString());
        stationLegendReminder = new Dialog(getActivity());

        Handler legend = new Handler();
        legend.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean legend_seen = NavigationActivity.tinyDB.getBoolean("legend_seen");
                role = NavigationActivity.tinyDB.getString("role");
                if (!role.equals("Driver")) {
                    if (legend_seen == false)
                        showStationsLegend(true);
                    else
                        showStationsLegend(false);
                }
            }
        }, 5000);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    public void run() {
                        while (progressStatus < 100) {
                            progressStatus += 5;
                            // Update the progress bar and display the
                            //current value in the text view
                            progress_handler.post(new Runnable() {
                                public void run() {
                                    loaderBar.setProgress(progressStatus);
                                    progress_ticker.setText(progressStatus + "/"+ loaderBar.getMax());
                                    if (progressStatus > 40)
                                        init.setText("Setting Variables...");
                                    if (progressStatus == 75)
                                        init.setText("Finishing...");
                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        loaderBar.setVisibility(View.INVISIBLE);
                        loaderBar.clearAnimation();
                        loader.setVisibility(View.INVISIBLE);
                    }
                }).start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        main.setVisibility(View.VISIBLE);
                        try {
                            mLocation = manager.getLastKnownLocation(provider);
                            if (mLocation != null) {

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

                                            }
                                            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                                    .findFragmentById(R.id.home_map_frags);
                                            mapFragment.getMapAsync(HomeFragment_Driver.this::onMapReady);
                                            GeoFireConfigSender();
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
                        } catch (Exception e) {
                            Log.e("exc", e.getMessage());
                        }
                    }
                }, 10000);
            }
        });
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
            NavigationActivity.tinyDB.putBoolean("legend_seen", true);
            stationLegendReminder.dismiss();
        });
    }

    static void send_bus_location (Location position, String bus_name, DatabaseReference ref) {
        Log.e("sent location for", bus_name);
        Bus_Tracker bus = new Bus_Tracker(new com.example.fypmetroapp.LatLng(position.getLatitude(), position.getLongitude()), bus_name);
        ref.setValue(bus);
    }

    View.OnClickListener track_buttons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.begin_btn:
                    updates.setTracking(true);
                    start_routing(getContext());
                    break;
                case R.id.stop_btn:
                    updates.setTracking(false);
                    stopTracking();
                    animateView(updates.tracking);
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
    private void stopTracking () {
        begin.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        if (!updates.tracking) {
            txtBusName.setText("...");
            proximity.setText("Waiting to");
            cur_stationTicker.setText("Receive Updates...");
            status.setText("Waiting to Receive Updates...");
            occupancy.setText("Waiting to Receive Updates...");
            status.setTextColor(Color.BLACK);
            occupancy.setTextColor(Color.BLACK);
            proximity.setTextColor(Color.BLACK);
            cur_stationTicker.setTextColor(Color.BLACK);
        }
    }

    @SuppressLint("SetTextI18n")
    public void initTracking_Bus () {
        begin.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);
        //Log.e("loc", userUpdates.nearest_station.name);
        ShowBusesDialog(station);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2000);
                //time = d/s [avg walking speed humans = 6kph]
                //TODO: CALCULATE THIS USING USER'S AVG SPEED IF ON FOOT/DRIVING/CYCLING
                proximity.setTextColor(getResources().getColor(R.color.normal_green));
                cur_stationTicker.setText(station.name);
                cur_stationTicker.setTextColor(Color.BLUE);
            }
        }, 2000);
    }

    private Task<Integer> getTimes(Station station, String bus) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firebaseFirestore
                .collection("stationdetails")
                .document("arrivals")
                .collection(station.type)
                .document(station.name)
                .collection(bus)
                .document("times");

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                ArrayList<String> times_reverse_order = new ArrayList<>();
                ArrayList<String> times_normal_order = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hmm");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
                String time;

                for (int i = 5; i >= 0; i--) {
                    time = snapshot.getString(String.valueOf(i));
                    try {
                        Date date = dateFormat.parse(time);
                        String out = dateFormat2.format(date);
                        times_reverse_order.add(out);
                    } catch (ParseException e) {
                        Log.e("exception", e.getMessage());
                    }
                }

                for (int i = 0; i < 6; i++) {
                    time = snapshot.getString(String.valueOf(i));
                    try {
                        Date date = dateFormat.parse(time);
                        String out = dateFormat2.format(date);
                        times_normal_order.add(out);
                    } catch (ParseException e) {
                        Log.e("exception", e.getMessage());
                    }
                }

                next_arrival(times_reverse_order, times_normal_order);

                //just change it to Bus;
                statType.setText(station.type);
                //next_arrivalTicker.setText("11:00am");
            }
        });

        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                DocumentSnapshot snapshot = value;
                Log.e("times", String.valueOf(snapshot));
                ArrayList<String> times_reverse_order = new ArrayList<>();
                ArrayList<String> times_normal_order = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hmm");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
                String time;

                for (int i = 5; i >= 0; i--) {
                    time = snapshot.getString(String.valueOf(i));
                    try {
                        Date date = dateFormat.parse(time);
                        String out = dateFormat2.format(date);
                        times_reverse_order.add(out);
                    } catch (ParseException e) {
                        Log.e("exception", e.getMessage());
                    }
                }

                for (int i = 0; i < 6; i++) {
                    time = snapshot.getString(String.valueOf(i));
                    try {
                        Date date = dateFormat.parse(time);
                        String out = dateFormat2.format(date);
                        times_normal_order.add(out);
                    } catch (ParseException e) {
                        Log.e("exception", e.getMessage());
                    }
                }

                next_arrival(times_reverse_order, times_normal_order);
            }
        });
        return null;
    }

    @SuppressLint("NewApi")
    private int next_arrival (ArrayList<String> times_reversed, ArrayList<String> times_normal) {
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime now = LocalTime.now();
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                String out;
                Date next1 = new Date();

                //loop forward array
                for (String time: times_reversed) {
                    try {
                        Date user = parser.parse(dtf.format(now));
                        Date next = parser.parse(time);
                        if (user.after(next)) {
                            //loop backwards array
                            for (String time1: times_normal) {
                                next1 = parser.parse(time1);
                                if (next.before(next1)) {
                                    out = parser.format(next1);
                                    Log.e("next at", out);
                                    break;
                                }
                            }
                            break;
                        }
                    } catch (ParseException e) {
                        Log.e("error", e.getMessage());
                    }
                }

                LocalTime date = next1.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                Duration timeElapsed = Duration.between(now, date);
                Log.e("diff", "Time taken: "+ timeElapsed.toMinutes() +" minutes");
                if (timeElapsed.toMinutes() <= 1) {
                    next_arrivalTicker.setText("Arriving...");
                } else
                    next_arrivalTicker.setText("< " + (timeElapsed.toMinutes() + 1) + " minutes");

                timeHandler.postDelayed(this, 15000);
            }
        }, 10);
        return 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void GeoFireConfigSender () {
        mDatabase = FirebaseDatabase.getInstance().getReference("tracking");
        userGeoFire = new GeoFire(mDatabase);
    }

    private void GeoFireConfigStations() {
        stationRef = FirebaseDatabase.getInstance().getReference("StationFences").child("Stations");
        stationGeoFire = new GeoFire(stationRef);
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
                mLocation = (manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
                if (mLocation != null) {

                    final double currentLatitude = mLocation.getLatitude();
                    final double currentLongitude = mLocation.getLongitude();
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        try {
            Log.e("SENT", "UPDATE");
        } catch (Exception e) {
            Log.e("stats", "null");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        //locationManager.requestLocationUpdates(provider, 500, 15, this);
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void ShowBusesDialog(Station station) {
        buses_at_station.setContentView(R.layout.choose_bus_dialog);
        txtBusName = buses_at_station.findViewById(R.id.txtBusStation_home);
        buses_at_station.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        buses_at_station.show();
        Extract_BUS_Data(station);
        //TableLayout buses = buses_at_station.findViewById(R.id.buses_at_station_home);
        //buses.removeAllViews();
    }

    @SuppressLint("NewApi")
    private void animateView (boolean animate) {
        ImageView occupancy_anim = getView().findViewById(R.id.occupancy_anim);
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.live_anim);
        // Insert your AnimatedVectorDrawable resource identifier
        occupancy_anim.setImageDrawable(d);
        if (animate)
            d.start();
        else if (!animate)
            d.stop();
    }

    public void Extract_BUS_Data(@NotNull Station station) {
        String getScheduleURL = "https://metromobile.000webhostapp.com/bus_stationLookUp.php?statName=" + station.name;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getScheduleURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSONS_BUSES(response);
            }
        }, error -> {

        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    private void showJSONS_BUSES(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject aTime = null;
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY_BUSES);
            TableLayout table_buses = buses_at_station.findViewById(R.id.buses_at_station_home);

            View inflated_buses = LayoutInflater.from(getContext()).inflate(R.layout.bus_to_inflate, table_buses, false);
            table_buses.addView(inflated_buses);

            FlexboxLayout flexboxBuses = table_buses.findViewById(R.id.flexboxBuses);
            flexboxBuses.removeAllViews();

            //show buses at station_bus
            for (int i = 0; i < result.length(); i++) {
                aTime = result.getJSONObject(i);

                TextView tvName = new TextView(flexboxBuses.getContext());
                tvName.setText(aTime.getString(Config.BUS_STATION_NAME));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10,10,10,10);
                tvName.setBackground(getContext().getResources().getDrawable(R.drawable.circle_box));
                tvName.setTextColor(Color.WHITE);
                tvName.setTextSize(20);
                tvName.setGravity(Gravity.CENTER);
                tvName.setPadding(5, 5, 5, 5);
                tvName.setLayoutParams(layoutParams);

                // now add to the Table.
                flexboxBuses.addView(tvName);
                tvName.setOnClickListener(v -> {
                    selected_bus = true;
                    String clickedBus = String.valueOf(tvName.getText());
                    getTimes(station, clickedBus);
                    updates.setTracking(selected_bus);
                    updates.setBus_name(clickedBus);
                    updates.setType("BUS");
                    buses_at_station.dismiss();
                    status.setText(clickedBus + " Is Sending Location Updates.");
                    ImageButton closeDialog = getView().findViewById(R.id.dialog_closeX);
                    ImageView occupancy_anim = getView().findViewById(R.id.occupancy_anim);
                    TextView textView = getView().findViewById(R.id.selected_bus);
                    textView.setText(clickedBus);
                    animateView(updates.tracking);
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void start_routing (Context context) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Option:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.choose_option);
        arrayAdapter.add("Submit Live Location For an LRV.");
        arrayAdapter.add("Submit Live Location For a Bus.");

        builderSingle.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        break;
                    case 1:
                        dialog.dismiss();
                        initTracking_Bus();
                        break;
                }
            }
        });
        builderSingle.show();
    }
}