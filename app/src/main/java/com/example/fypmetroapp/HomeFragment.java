package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //TextView locText;
    Location mLocation;
    LatLng mLocLatLng;
    GoogleMap gMap;
    int pre_total_distance = 0, user_dist_covered = 0, new_total_distance;
    GlobalProperties properties = new GlobalProperties();
    UserUpdates userUpdates = new UserUpdates();
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference userRef;
    FloatingActionButton fabDirections;
    LocationCallback locationCallback;
    GeoFire userGeoFire;
    boolean travelling = false;
    Button begin_btn;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directions, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //locText = getView().findViewById(R.id.locText);

        //on load pan camera to user's location
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));

        if (ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = manager.getLastKnownLocation(bestProvider);
        if (mLocation != null) {
            userUpdates.setLocation(mLocation);
            userUpdates.setLatLngLocation(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            LatLng userLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            String userLocation = getAddress(getContext(), mLocation.getLatitude(), mLocation.getLongitude());
            //locText.setText(userLocation);

            LatLng destination_example = new LatLng(-20.2653072, 57.4783713);
            userUpdates.setLatLngDestination(destination_example);
            //Log.e("destination", userUpdates.latLngDestination.toString());
            pre_total_distance = (int) SphericalUtil.computeDistanceBetween(userLatLng, destination_example);
            userUpdates.setPre_total_dist(pre_total_distance);
        }

        begin_btn = getView().findViewById(R.id.begin_btn);
        begin_btn.setOnClickListener(v -> {
            begin(v);
        });

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        buildLocationRequest();
                        buildLocationCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.home_map_frags);
                        mapFragment.getMapAsync(HomeFragment.this::onMapReady);
                        GeoFireConfig();
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

    public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        String userLoc = "";
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


                userLoc = addresses.get(0).getLocality() + ", "+addresses.get(0).getCountryName();
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
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

    public void begin (View view) {
        user_dist_covered = 0;
        progressBar = (ProgressBar) getView().findViewById(R.id.distance_bar);
        new ProgressSet().execute();
    }

    public class ProgressSet extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if (pre_total_distance != 0) {
                for (int c = userUpdates.getCovered_dist(); c <= userUpdates.getPre_total_dist(); c += user_dist_covered) {
                    if (c > 0) {
                        SystemClock.sleep(100);

                        new_total_distance = (int) SphericalUtil.computeDistanceBetween(userUpdates.latLngLocation, userUpdates.latLngDestination);
                        userUpdates.setNew_total_dist(new_total_distance);
                        Log.e("pre", String.valueOf(userUpdates.getPre_total_dist()));
                        Log.e("new", String.valueOf(userUpdates.getNew_total_dist()));

                        int pre_user_dist_covered = userUpdates.getPre_total_dist() - userUpdates.getNew_total_dist();
                        user_dist_covered += pre_user_dist_covered;
                        userUpdates.setCovered_dist(user_dist_covered);
                        Log.e("covered", String.valueOf(user_dist_covered));

                        publishProgress(user_dist_covered);
                        if (user_dist_covered == userUpdates.getPre_total_dist())
                            Toast.makeText(getContext(), "Reached Destination", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return "Completed!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    private void GeoFireConfig() {
        userRef = FirebaseDatabase.getInstance().getReference("lrtmateapp");
        userGeoFire = new GeoFire(userRef);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                gMap = properties.getgMap();
                if (gMap != null) {

                    userGeoFire.setLocation("User",new GeoLocation(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()));

                    LatLng currentLatLngLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    Location currentLocation = new Location(locationResult.getLastLocation());

                    userUpdates.setLocation(currentLocation);
                    userUpdates.setLatLngLocation(currentLatLngLocation);

                    //Log.e("previous distance", String.valueOf(pre_total_distance));
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2500);
        locationRequest.setSmallestDisplacement(10f);
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
                userUpdates.setLocation(manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
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