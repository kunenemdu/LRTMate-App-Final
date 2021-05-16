package com.example.fypmetroapp;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.green;

public class MapsNewer extends Fragment implements GeoQueryDataEventListener {

    //Variable Declarations
    public static GoogleMap gMap;
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker lrtMarker;
    private Marker busMarker;
    TextView titleText;
    private View locationButton;
    private View searchButton;
    TextView stopText;
    private TextView stopDistanceText;
    private TextView txtClicked_LRT_Station;
    private TextView txtClicked_BUS_Station;
    private boolean permissionDenied = false;
    Marker marker;
    private Marker poiMarker;
    LatLng curLocation;
    TextView textAddress;
    LinearLayout llNearbySheet;
    LinearLayout llBus_Stations_Route;
    LinearLayout llBottomSheet;
    LinearLayout llLRT_StationSheet_Sche;
    LinearLayout llBUS_StationSheet_Sche;
    RelativeLayout rlDirections;
    private Polyline polyline_LRT;
    private Polyline polyline_BUS;
    BottomSheetBehavior bottomSheetBehavior_NearBy;
    BottomSheetBehavior bottomSheetBehavior_Buses_Stations_Route;
    BottomSheetBehavior bottomSheetBehavior_Directions;
    BottomSheetBehavior bottomSheetBehavior_LRT_ClickedStation_Sche;
    BottomSheetBehavior bottomSheetBehavior_BUS_ClickedStation_Sche;
    BottomSheetBehavior bottomSheetBehavior_directionsBottom;
    FloatingActionButton fabDirections;
    LocationCallback locationCallback;
    ArrayList<Marker> allLRTMarkers;
    ArrayList<Marker> allBusMarkers;
    ArrayList<Marker> allMarkers;
    ArrayList<Station> allBusStations, allLRTStations;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    DatabaseReference userRef;
    DatabaseReference driverRef;
    TextView timeText;
    TextView directionText;
    TextView busText, busAtStation;
    TextView clickedBus;
    ImageButton directionsButton;
    public static final int overview = 0;
    CardView nearbyCardView;
    CardView drivingMode;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<Bus> allBuses;
    int distance = 0;
    GeoFire userGeoFire;
    GeoFire driverGeoFire;
    public static ArrayList<Polyline> route = new ArrayList<>();
    SharedPreferences preferences;
    LocationRequest locationRequest;
    Marker userMarker;
    Marker driverMarker;
    Fragment active;
    Dialog stationDetailsDialog;
    GlobalProperties properties = new GlobalProperties();
    FirebaseAuth firebaseAuth;
    public static String uid;
    public static String driverID;
    public static List<StationFence> stationsFences;
    ArrayList<Station> _approaching;
    ArrayList<StationFence> foundfences;

    //Class Declarations
    Origin origin = new Origin();
    Destination destination = new Destination();
    UserUpdates userUpdates = new UserUpdates();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nearby_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        stationDetailsDialog = new Dialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (gMap != null) {
                    uid = firebaseAuth.getCurrentUser().getUid();

                    userGeoFire.setLocation(uid, new GeoLocation(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()), (key, error) -> {
                                if (userMarker != null) userMarker.remove();

                                userMarker = gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                                locationResult.getLastLocation().getLongitude()))
                                        .visible(false)
                                        .title(uid));

                                curLocation = userMarker.getPosition();
                                userUpdates.setLatLngLocation(curLocation);
                                userUpdates.setLocation(locationResult.getLastLocation());
                                ShowNearestStations();
                                gMap.animateCamera(CameraUpdateFactory
                                        .newLatLngZoom(userMarker.getPosition(), 13.0f));
                                initStationsFences();
                                //area identifiers
                                for (StationFence stationFence: getStationsFences()){
                                    gMap.addCircle(new CircleOptions().center(stationFence.stationLocation)
                                            .radius(30)//metres
                                            .strokeColor(Color.BLUE)
                                            .fillColor(0x220000FF)
                                            .strokeWidth(1.0f));

                                    //do this when user enters GeoFence
                                    GeoQuery query = userGeoFire.queryAtLocation(
                                            new GeoLocation(stationFence.stationLocation.latitude, stationFence.stationLocation.longitude), 0.15f);
                                    query.addGeoQueryDataEventListener(MapsNewer.this);
                                }
                            });
                }
            }
        };
    }

    private void buildDriverLocationCallback() {
        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            public void onLocationResult(LocationResult locationResult) {
                if (gMap != null) {
                    driverID = firebaseAuth.getCurrentUser().getUid();
                    driverGeoFire.setLocation(driverID, new GeoLocation(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()), (key, error) -> {
                                if (driverMarker != null) driverMarker.remove();

                                driverMarker = gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                                locationResult.getLastLocation().getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));

                                Log.e("bus loc", String.valueOf(driverMarker.getPosition()));

                            });
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setSmallestDisplacement(25f);
    }

    private void buildDriverLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(2500);
        locationRequest.setSmallestDisplacement(25f);
    }

    @SuppressLint({"ResourceType", "NewApi"})
    public void onMapReady(GoogleMap googleMap) {
        //initialise map for use
        gMap = googleMap;
        properties.setgMap(gMap);

        gMap = Tools.configActivityMaps(googleMap);

        //call location before doing anything with it
        enableMyLocation();
        if (userUpdates.location != null) {
            String origin = String.valueOf(userUpdates.location.getLatitude()) + "," + String.valueOf(userUpdates.location.getLongitude());

        }

        LatLngBounds mauritius = new LatLngBounds(
                new LatLng(-20.523707, 57.277314),
                new LatLng(-20.000119, 57.847562)
        );

        gMap.setOnMarkerClickListener(MapsNewer.this::onMarkerClick);

        try {
            /*
             Customise the styling of the base map using a JSON object defined
             in a raw resource file.
            */
            boolean success = gMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            //location of my json styles
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "No style found. Error: ", e);
        }

        //initialise ImageView
        locationButton = getView().findViewById(0x2);
        searchButton = getView().findViewById(R.id.places_autocomplete_search_button);

        // Change the visibility of my location button
        if (locationButton != null)
            locationButton.setVisibility(View.GONE);

        // Change the visibility of my location button
        if (searchButton != null)
            searchButton.setVisibility(View.GONE);

        //implement listener for ImageView
        getView().findViewById(R.id.imMyLocation).setOnClickListener(v -> {
            if (gMap != null) {
                if (locationButton != null)
                    //clicking ImageView calls Google's location button which is hidden
                    locationButton.callOnClick();
            }
        });

        //implement listener for ImageView
        getView().findViewById(R.id.searchIcon).setOnClickListener(v -> {
            if (gMap != null) {
                if (searchButton != null)
                    //clicking ImageView calls Google's search button which is hidden
                    searchButton.callOnClick();
            }
        });

        gMap.setOnMyLocationButtonClickListener(() -> {
            Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            gMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
            return false;
        });
        gMap.setOnMyLocationClickListener(location -> {
            Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
            //setmLocation(location);
        });



        gMap.setOnPoiClickListener(pointOfInterest -> {
            bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //if place selected previously
            if (poiMarker != null) {
                //if new place selected
                if (poiMarker.getPosition() != pointOfInterest.latLng) {
                    //remove old place add new marker
                    poiMarker.remove();
                    poiMarker = gMap.addMarker(new MarkerOptions()
                            //set marker position to place LatLng
                            .position(pointOfInterest.latLng)
                            //set marker name to selected place
                            .title(pointOfInterest.name)
                    );
                } else
                    poiMarker = null;
            }

            //if place marker not set
            if (poiMarker == null) {
                //add a marker on map
                poiMarker = gMap.addMarker(new MarkerOptions()
                        //set marker position to place LatLng
                        .position(pointOfInterest.latLng)

                        //set marker name to selected place
                        .title(pointOfInterest.name)
                );

            }
            titleText.setText(pointOfInterest.name);
            textAddress.setText(pointOfInterest.latLng.toString());
        });


        gMap.setOnMapLoadedCallback(() -> {

            //gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mauritius, 30));

            gMap.setLatLngBoundsForCameraTarget(mauritius);

        });

        LRT_Polyline();
        addRailStations();
        addBusStations();

        gMap.setOnMapLongClickListener(latLng -> gMap.addMarker(new MarkerOptions()
                .position(latLng)
        ));

        //TODO: MAP CLICK LISTENER
        gMap.setOnMapClickListener(latLng -> {
            removeLRTPoly();
            removeBUSPoly();
            //hide fabD button and sheet
            if ((bottomSheetBehavior_Directions.getState() == BottomSheetBehavior.STATE_COLLAPSED)) {
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                fabDirections.hide();
            }

            //collapse D sheet
            if (bottomSheetBehavior_Directions.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if ((bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_COLLAPSED))
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);

            if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED
                    || bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (bottomSheetBehavior_Directions.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (bottomSheetBehavior_LRT_ClickedStation_Sche.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_COLLAPSED);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (bottomSheetBehavior_LRT_ClickedStation_Sche.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (bottomSheetBehavior_BUS_ClickedStation_Sche.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_COLLAPSED);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (bottomSheetBehavior_BUS_ClickedStation_Sche.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (nearbyCardView.getVisibility() == View.INVISIBLE)
                nearbyCardView.setVisibility(View.VISIBLE);

        });
        if (getPolyline_LRT() != null)
            getPolyline_LRT().setVisible(false);

        addBuses();
        walkToStation();

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            ShowNearestStations();
        }
    }

    @Override
    public void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        active = this;
        foundfences = new ArrayList<>();

        fabDirections = getView().findViewById(R.id.fab_directions);
        textAddress = getView().findViewById(R.id.textAddress);
        titleText = getView().findViewById(R.id.titleText);
        txtClicked_LRT_Station = getView().findViewById(R.id.txtSelectedtation);
        timeText = null;
        directionText = null;
        nearbyCardView = getView().findViewById(R.id.nearbystations);
        directionsButton = getView().findViewById(R.id.startDirections);
        drivingMode = getView().findViewById(R.id.drivingMode);

        directionsButton.setOnClickListener(Buttons);
        nearbyCardView.setOnClickListener(Buttons);
        drivingMode.setOnClickListener(Buttons);

        ((FloatingActionButton) getView().findViewById(R.id.fab_directions)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (bottomSheetBehavior_Directions.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_EXPANDED);
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    }
                } catch (Exception e) {
                }
            }
        });

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("UserPrefs", Config.MODE_PRIVATE);
        String role = pref.getString("role", null);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (role.equals("Driver")) {
                            buildDriverLocationRequest();
                            buildDriverLocationCallback();
                        } else if (role.equals("User")) {
                            buildLocationRequest();
                            buildLocationCallback();
                        }
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.mainMapFrags);
                        mapFragment.getMapAsync(MapsNewer.this::onMapReady);
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

        initMarkerComponent();
        initNearByComponent();
        initLRT_StationScheduleSheet();
        initBottomDirectionsComponent();
        initAutoFrags();
        initBus_StationScheduleSheet();
        initClicked_BUS_Component();
        clickedBus = getView().findViewById(R.id.clickedBus);
    }

    private void initStationsFences() {
        stationsFences = new ArrayList<>();

        for (Marker this_marker: getAllMarkers()){
            LatLng statLoc = new LatLng(this_marker.getPosition().latitude, this_marker.getPosition().longitude);
            String statName = this_marker.getTitle();
            String type = (String) this_marker.getTag();

            //instantiate the station GeoFence
            StationFence stationFence = new StationFence(statName, statLoc, type);

            stationsFences.add(stationFence);
            /*FirebaseDatabase.getInstance()
                    .getReference("StationFences")
                    .child("Stations").child(stationFence.stationName).setValue(stationFence.stationLocation)
                    .addOnCompleteListener(task -> Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show());

            FirebaseDatabase.getInstance()
                    .getReference("StationFences")
                    .child("Stations").child(stationFence.stationName).child("type").setValue(stationFence.type)
                    .addOnCompleteListener(task -> Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show());*/
        }
        setStationsFences(stationsFences);
    }

    public static List<StationFence> getStationsFences() {
        return stationsFences;
    }

    public void setStationsFences(List<StationFence> stationsFences) {
        this.stationsFences = stationsFences;
    }

    private void GeoFireConfig() {
        userRef = FirebaseDatabase.getInstance().getReference("User");
        driverRef = FirebaseDatabase.getInstance().getReference("Driver");
        userGeoFire = new GeoFire(userRef);
        driverGeoFire = new GeoFire(driverRef);
    }

    public ArrayList<Marker> getAllMarkers() {
        ArrayList<Marker> markers = new ArrayList<>();
        for (int i = 0; i < allLRTMarkers.size(); i++) {
            markers.add(allLRTMarkers.get(i));
        }
        for (int y = 0; y < allBusMarkers.size(); y++) {
            markers.add(allBusMarkers.get(y));
        }
        return markers;
    }

    public void setAllMarkers(ArrayList<Marker> allMarkers) {
        this.allMarkers = allMarkers;
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        Marker origin = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng))
                .title(results.routes[overview].legs[overview].startAddress)
                .snippet("Origin"));
        Marker destination = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng))
                .title(results.routes[overview].legs[overview].endAddress)
                .snippet(getEndLocationTitle(results)));

        origin.setTag("O");
        destination.setTag("D");
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 8));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private Polyline addBusPolyline(DirectionsResult results, GoogleMap mMap) {
        // Draw a dashed (60px spaced) blue polyline
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        polyline_BUS = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        polyline_BUS.setColor(Color.BLUE);
        polyline_BUS.setWidth(20f);
        setPolyline_BUS(polyline_BUS);

        return polyline_BUS;
    }

    private void addWalkToBusPolyline(DirectionsResult results, GoogleMap mMap) {
        // Draw a dashed (60px spaced) blue polyline
        List<PatternItem> dashedPattern = Arrays.asList(new Dot(), new Gap(60));
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        Polyline walk = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        walk.setColor(Color.BLUE);
        walk.setPattern(dashedPattern);
        walk.setWidth(20f);
    }

    private void addLRTPolyline(DirectionsResult results, GoogleMap mMap) {
        // Draw a dashed (60px spaced) blue polyline
        List<PatternItem> dashedPattern = Arrays.asList(new Dash(60), new Gap(60));
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        polyline_LRT = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        polyline_LRT.setColor(this.getContext().getResources().getColor(R.color.quantum_googred));
        polyline_LRT.setPattern(dashedPattern);
        setPolyline_LRT(polyline_LRT);
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DirectionsResult getWalkingDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(Config.MYAPI_KEY)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    //abusing Google's DirectionsAPI for Metro Route
    public void LRT_Polyline() {
        DirectionsResult results = getDirectionsDetails("Rose Hill Central, Beau Bassin-Rose Hill", "Port Louis Victoria, Port Louis", TravelMode.TRANSIT);
        if (results != null) {
            addLRTPolyline(results, gMap);
            positionCamera(results.routes[overview], gMap);
        }
    }

    public void initAutoFrags() {

        /*
         * AUTO COMPLETE SEARCH BAR */
        // Initialize the SDK
        Places.initialize(this.getContext(), Config.MYAPI_KEY);
        //Places.initialize(getApplicationContext(), "AIzaSyAYWL_z-TJOBptGAzkXbVB2ZE_NhP27Yx4");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this.getContext());
        //PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragmentDestination = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_destination);


        AutocompleteSupportFragment autocompleteFragmentOrigin = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_origin);




        //TODO: autocomplete search customiser
        autocompleteFragmentDestination.setCountry("MU");
        autocompleteFragmentOrigin.setCountry("MU");
        autocompleteFragmentDestination.setHint("Where are you going?");
        autocompleteFragmentOrigin.setHint("Where are you starting?");
        EditText edtText = autocompleteFragmentOrigin.getView().findViewById(R.id.places_autocomplete_search_input);
        edtText.setTextSize(14.0f);
        EditText inputText = getView().findViewById(R.id.places_autocomplete_search_input);
        inputText.setTextSize(14.0f);
        ImageView ivSearchTo = autocompleteFragmentDestination.getView().findViewById(R.id.places_autocomplete_search_button);
        ImageView ivSearchFrom = autocompleteFragmentOrigin.getView().findViewById(R.id.places_autocomplete_search_button);
        ivSearchTo.setBackgroundColor(Color.BLACK);
        ivSearchTo.setVisibility(View.GONE);
        ivSearchFrom.setBackgroundColor(Color.BLACK);
        ivSearchFrom.setVisibility(View.GONE);


        // Specify the types of place data to return.
        autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                LinearLayout llBottomSheet = (LinearLayout) getView().findViewById(R.id.bottom_sheet);
                titleText = llBottomSheet.findViewById(R.id.titleText);
                textAddress = llBottomSheet.findViewById(R.id.textAddress);
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                LatLng latLng = place.getLatLng();
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                String user_Destination = temp.getLatitude() + "," + temp.getLongitude();
                destination.setDestination(user_Destination);
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                LinearLayout llBottomSheet = (LinearLayout) getView().findViewById(R.id.bottom_sheet);
                titleText = llBottomSheet.findViewById(R.id.titleText);
                textAddress = llBottomSheet.findViewById(R.id.textAddress);
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                LatLng latLng = place.getLatLng();
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                String user_origin = temp.getLatitude() + "," + temp.getLongitude();
                origin.setOrigin(user_origin);
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });


    }

    //event listener for buttons
    android.view.View.OnClickListener Buttons = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.startDirections:
                    bottomSheetBehavior_directionsBottom.setState(BottomSheetBehavior.STATE_EXPANDED);
                    break;
                case R.id.nearbystations:
                    ShowNearestStations();
                    if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_HIDDEN)
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_EXPANDED)
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    if (bottomSheetBehavior_directionsBottom.getState() == BottomSheetBehavior.STATE_EXPANDED)
                        nearbyCardView.setVisibility(View.INVISIBLE);
                    break;
                case R.id.drivingMode:
                    DirectionsResult results = getDirectionsDetails(origin.getOrigin(), destination.getDestination(), TravelMode.DRIVING);
                    if (results != null) {
                        addPolyline(results, gMap);
                        positionCamera(results.routes[overview], gMap);
                        addMarkersToMap(results, gMap);
                        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                        bottomSheetBehavior_directionsBottom.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        nearbyCardView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    private void initMarkerComponent() {
        // get the bottom sheet view
        llBottomSheet = (LinearLayout) getView().findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior_Directions = BottomSheetBehavior.from(llBottomSheet);

        // change the state of the bottom sheet
        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
        fabDirections.hide();

        // set callback for changes
        bottomSheetBehavior_Directions.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fabDirections.hide();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_Directions.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        gMap.setPadding(0, 0, 0, (int) off);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    //reposition marker at the center
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fabDirections.hide();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (marker != null)
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        gMap.setPadding(0, 0, 0, (int) off);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                }
            }
        });

    }

    private void initNearByComponent() {
        //get the view pager and add fragments to it at runtime
        viewPager = getView().findViewById(R.id.viewpager_nearby);
        tabLayout = getView().findViewById(R.id.tabLayout_nearby);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new NearbyStationsFragment(), "Light Rail");
        adapter.addFragment(new NearbyBusesFragment(), "Buses");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        // get the bottom sheet view
        llNearbySheet = getView().findViewById(R.id.bottom_nearby_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior_NearBy = BottomSheetBehavior.from(llNearbySheet);

        // change the state of the bottom sheet
        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set callback for changes
        bottomSheetBehavior_NearBy.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nearbyCardView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_NearBy.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        setMapPadding(off);
                        break;
                }
            }
        });

    }

    private void initClicked_BUS_Component() {
        // get the bottom sheet view
        llBus_Stations_Route = getView().findViewById(R.id.route_stations);

        // init the bottom sheet behavior
        bottomSheetBehavior_Buses_Stations_Route = BottomSheetBehavior.from(llBus_Stations_Route);

        // change the state of the bottom sheet
        bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set callback for changes
        bottomSheetBehavior_Buses_Stations_Route.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nearbyCardView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_Buses_Stations_Route.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        setMapPadding(off);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    //reposition marker at the center
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                }
            }
        });

    }

    private void initLRT_StationScheduleSheet() {
        //get the view pager and add fragments to it at runtime
        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new ToPortLouisFragment(), "To Port Louis");
        adapter.addFragment(new ToRoseHillFragment(), "To Rose Hill");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        // get the bottom sheet view
        llLRT_StationSheet_Sche = getView().findViewById(R.id.bottom_station_sheet_sche);

        // init the bottom sheet behavior
        bottomSheetBehavior_LRT_ClickedStation_Sche = BottomSheetBehavior.from(llLRT_StationSheet_Sche);

        // change the state of the bottom sheet
        bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set callback for changes
        bottomSheetBehavior_LRT_ClickedStation_Sche.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nearbyCardView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_LRT_ClickedStation_Sche.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        gMap.setPadding(0, 0, 0, (int) off);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    //reposition marker at the center
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                }
            }
        });

    }

    private void initBus_StationScheduleSheet () {
        // get the bottom sheet view
        llBUS_StationSheet_Sche = getView().findViewById(R.id.bottom_bus_station_sche);
        //get the view pager and add fragments to it at runtime
        viewPager = llBUS_StationSheet_Sche.findViewById(R.id.viewpager_bus);
        tabLayout = llBUS_StationSheet_Sche.findViewById(R.id.tabLayout_bus);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentBusesAtStation(), "Bus Lines");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // init the bottom sheet behavior
        bottomSheetBehavior_BUS_ClickedStation_Sche = BottomSheetBehavior.from(llBUS_StationSheet_Sche);

        // change the state of the bottom sheet
        bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set callback for changes
        bottomSheetBehavior_BUS_ClickedStation_Sche.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nearbyCardView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_BUS_ClickedStation_Sche.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        gMap.setPadding(0, 0, 0, (int) off);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    //reposition marker at the center
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                }
            }
        });

    }

    private void initBottomDirectionsComponent() {
        rlDirections = getView().findViewById(R.id.directions_bottom);
        bottomSheetBehavior_directionsBottom = BottomSheetBehavior.from(rlDirections);
        bottomSheetBehavior_directionsBottom.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior_directionsBottom.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nearbyCardView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getMeasuredHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_directionsBottom.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //mapFragment.getView().setPadding(0, 0, 0, (int) off);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //mapFragment.getView().setPadding(0, 0, 0, 0);
                        break;
                }
            }
        });
    }

    public Polyline getPolyline_BUS() {
        return polyline_BUS;
    }

    public void setPolyline_BUS(Polyline polyline_BUS) {
        this.polyline_BUS = polyline_BUS;
    }

    public Polyline getPolyline_LRT() {
        return polyline_LRT;
    }

    public void setPolyline_LRT(Polyline polyline_LRT) {
        this.polyline_LRT = polyline_LRT;
    }

    @SuppressLint("NewApi")
    private void ShowNearestStations() {
        allBuses = addBuses();
        TableLayout mainTable_stations = getView().findViewById(R.id.mainTable_stations);
        TableLayout mainTable_buses = getView().findViewById(R.id.mainTable_buses);

        if (mainTable_stations != null) mainTable_stations.removeAllViews();
        if (mainTable_buses != null) mainTable_buses.removeAllViews();

        //nearest stations
        ArrayList<Station> nearbyStations = new ArrayList<>();
        allMarkers = new ArrayList<>();
        int distance = 0;

        for (int i = 0; i < allLRTMarkers.size(); i++) {
            Marker LRTmarker = allLRTMarkers.get(i);
            allMarkers.add(LRTmarker);
            if (userUpdates.latLngLocation != null) {
                if (userMarker != null) {
                    curLocation = userUpdates.latLngLocation;
                    distance = (int) SphericalUtil.computeDistanceBetween(curLocation, LRTmarker.getPosition());
                }
            }

            Station LRTstation = new Station();
            LRTstation.name = LRTmarker.getTitle();
            LRTstation.distance = distance;
            LRTstation.position = LRTmarker.getPosition();
            LRTstation.type = "LRT";
            if (distance <= 1000)
                nearbyStations.add(LRTstation);
        }
        for (int y = 0; y < allBusMarkers.size(); y++) {
            Marker BUSmarker = allBusMarkers.get(y);

            allMarkers.add(BUSmarker);
            if (userUpdates.latLngLocation != null) {
                if (userMarker != null) {
                    curLocation = userUpdates.latLngLocation;
                    distance = (int) SphericalUtil.computeDistanceBetween(curLocation, BUSmarker.getPosition());
                }
            }

            Station BUSstation = new Station();
            BUSstation.name = BUSmarker.getTitle();
            BUSstation.distance = distance;
            BUSstation.position = BUSmarker.getPosition();
            BUSstation.type = "BUS";
            if (distance <= 1000)
                nearbyStations.add(BUSstation);
        }

        setAllMarkers(allMarkers);
        //sort by closest distance
        Collections.sort(nearbyStations, new SortByDistance());
        int green = getResources().getColor(R.color.quantum_googgreen);
        //inflate each LRT station to view
        for (int x = 0; x < nearbyStations.size(); x++) {
            Station station = nearbyStations.get(x);

            LayoutInflater inflater = MapsNewer.this.getLayoutInflater();
            TableRow mainRow = new TableRow(MapsNewer.this.getContext());

            if (station.type.equals(Config.STATION_TYPE_LRT)) {
                inflater.inflate(R.layout.lrt_row_to_inflate, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());
                //int distance_to_nearest = (int) SphericalUtil.computeDistanceBetween(curLocation, station.distance);
                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                stopDistanceText.setText("\tless than: " + station.distance + "m away");
                stopDistanceText.setTextColor(green);

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.station_text);
                stopText.setPadding(0, 40, 0, 0);

                mainRow.addView(stopText);
                mainRow.addView(stopDistanceText);
                //onclick listener for each row
                mainRow.setOnClickListener(v -> ClickedNearbyStation(station.name));
                if (mainTable_stations != null) mainTable_stations.addView(mainRow);
            } else if (station.type.equals(Config.STATION_TYPE_BUS)) {
                inflater.inflate(R.layout.bus_row_to_inflate, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());
                busText = new TextView(mainRow.getContext());

                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                stopDistanceText.setText("\tless than: " + station.distance + "m away");
                stopDistanceText.setTextColor(green);

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.station_text);
                stopText.setPadding(0, 40, 0, 0);

                //TODO: ADD BUS STATIONS # ON EACH ROW
                /*for (int i = 0; i < allBuses.size(); i++) {
                    Bus bus = allBuses.get(i);
                    ArrayList<Station> stations = bus.getStops();

                    for (int j = 0; j < stations.size(); j++) {
                        Station station1 = stations.get(j);

                        if (station1.getName().equals(station.getName())) {
                            busText.setText(String.valueOf(bus.getName()));
                        }
                    }
                }*/

                mainRow.addView(stopText);
                mainRow.addView(busText);
                mainRow.addView(stopDistanceText);

                //onclick listener for each row
                mainRow.setOnClickListener(v -> ClickedNearbyStation(station.name));
                if (mainTable_buses != null) mainTable_buses.addView(mainRow);
            }
        }
    }

    private void ShowStationDialog(Marker marker) {
        stationDetailsDialog.setContentView(R.layout.dialog_clicked_station);
        txtClicked_BUS_Station = stationDetailsDialog.findViewById(R.id.txtBusStation_new);
        txtClicked_BUS_Station.setText(marker.getTitle());
        stationDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        ImageButton closeDialog = stationDetailsDialog.findViewById(R.id.dialog_closeX);
        Extract_BUS_Data(marker);
        TableLayout buses = getView().findViewById(R.id.buses_at_station);
        buses.removeAllViews();
        stationDetailsDialog.show();
        closeDialog.setOnClickListener(v -> stationDetailsDialog.dismiss());
    }

    //show the stations for a bus route
    @SuppressLint("NewApi")
    private void ShowRouteStations(Bus bus) {
        TableLayout mainTable_stations = getView().findViewById(R.id.mainTable_Route);
        mainTable_stations.removeAllViews();
        clickedBus.setText(String.valueOf(bus.getName()));
        //nearest stations
        ArrayList<Station> this_buses_stops = bus.getStops();
        ArrayList<Station> nearbyStations = new ArrayList<>();
        int distance = 0;

        for (int y = 0; y < this_buses_stops.size(); y++) {
            Station station = this_buses_stops.get(y);
            if (userUpdates.location != null) {
                curLocation = new LatLng(userUpdates.location.getLatitude(), userUpdates.location.getLongitude());
                distance = (int) SphericalUtil.computeDistanceBetween(curLocation, station.position);
            }

            Station BUSstation = new Station();
            BUSstation.name = station.name;
            BUSstation.distance = distance;
            BUSstation.position = station.position;
            BUSstation.type = "BUS";
            nearbyStations.add(BUSstation);
        }

        //sort by closest distance
        Collections.sort(nearbyStations, new SortByDistance());

        //inflate each LRT station to view
        for (int x = 0; x < nearbyStations.size(); x++) {
            Station station = nearbyStations.get(x);

            LayoutInflater inflater = MapsNewer.this.getLayoutInflater();
            TableRow mainRow = new TableRow(MapsNewer.this.getContext());

            if (station.type == Config.STATION_TYPE_BUS) {
                inflater.inflate(R.layout.bus_route_stations, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());
                busText = new TextView(mainRow.getContext());

                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                //stopDistanceText.setText("~" + station.distance + "m away");

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.station_text);
                stopText.setPadding(5, 40, 0, 0);

                mainRow.addView(stopText);
                //mainRow.addView(stopDistanceText);

                //onclick listener for each row
                mainRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClickedNearbyStation(station.name);
                    }
                });
                mainTable_stations.addView(mainRow);
            }
        }
    }

    public void removeLRTPoly() {
        if (getPolyline_LRT() != null) {
            if (getPolyline_LRT().isVisible() == true) {
                getPolyline_LRT().setVisible(false);
            }
        }
    }

    public void removeBUSPoly() {
        //remove whole bus POLYLINE
        if (route != null) {
            for (Polyline line : route) {
                line.remove();
            }
            route.clear();
        }
    }

    public boolean onMarkerClick(Marker marker) {
        try {
            if (marker.getTag().equals("P")) {
                titleText.setText(marker.getTitle());
                textAddress.setText(marker.getPosition().toString());
                //show it as collapsed
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //hide all the other bottom sheets
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            } else if (marker.getTag().equals("LRT")) {
                txtClicked_LRT_Station.setText(marker.getTitle());
                //draw the LRT polyline on click
                polyline_LRT.setVisible(true);
                //show clicked station sheet as half expanded
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

                //hide the rest of the bottom sheets
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                fabDirections.hide();
                nearbyCardView.setVisibility(View.INVISIBLE);
                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                onLRT_StationClick(marker);
            } else if (marker.getTag().equals("BUS")) {

                //draw the BUS polyline on click
                /*for (int i = 0; i < allBuses.size(); i++) {
                    Bus bus = allBuses.get(i);
                    ArrayList<Station> stations = bus.getStops();

                    for (int j = 0; j < stations.size(); j++) {
                        Station station1 = stations.get(j);

                        if (station1.getName().equals(marker.getTitle())) {
                            //Log.e("Bus is:", String.valueOf(bus.getName()));
                            //Log.e("stops for this bus ", station1.getName());
                        }
                    }
                }*/
                /*show clicked station sheet as half expanded
                bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                hide the rest of the bottom sheets
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                fabDirections.hide();*/


                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                onBUS_StationClick(marker);
                ShowStationDialog(marker);
            }
            else if (marker.getTag().equals("D")) marker.showInfoWindow();
            else if (marker.getTag().equals("O")) marker.showInfoWindow();

        } catch (Exception e) {
        }
        return true;
    }

    public ArrayList<String> GetAllLRTNames() {
        ArrayList<String> allLRTStationNames = new ArrayList<>();
        for (int i = 0; i < allLRTMarkers.size(); i++) {
            String stationName = allLRTMarkers.get(i).getTitle();
            allLRTStationNames.add(stationName);
        }
        return allLRTStationNames;
    }

    public ArrayList<String> GetAllBUSNames() {
        ArrayList<String> allBUSStationNames = new ArrayList<>();
        for (int i = 0; i < allBusMarkers.size(); i++) {
            String stationName = allBusMarkers.get(i).getTitle();
            allBUSStationNames.add(stationName);
        }
        return allBUSStationNames;
    }

    //get the station user clicked
    public void ClickedNearbyStation(String name) {
        nearbyCardView.setVisibility(View.INVISIBLE);
        if (GetAllLRTNames().contains(name)) {
            for (int i = 0; i < allLRTMarkers.size(); i++) {
                Marker Trainmarker = allLRTMarkers.get(i);
                //if the station name is the same as the marker call OnMarkerClick
                if (name.equals(Trainmarker.getTitle())) {
                    onMarkerClick(Trainmarker);
                }
            }
        } else if (GetAllBUSNames().contains(name)) {
            for (int i = 0; i < allBusMarkers.size(); i++) {
                Marker Busmarker = allBusMarkers.get(i);
                //if the station name is the same as the marker call OnMarkerClick
                if (name.equals(Busmarker.getTitle())) {
                    if (bottomSheetBehavior_NearBy.getState() != BottomSheetBehavior.STATE_HIDDEN){
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                        onMarkerClick(Busmarker);
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void onLRT_StationClick(Marker marker) {
        Extract_LRT_Data(marker);
        TableLayout scheTable_RH = getView().findViewById(R.id.scheTable_RH);
        TableLayout scheTable_PL = getView().findViewById(R.id.scheTable_PL);
        scheTable_RH.removeAllViews();
        scheTable_PL.removeAllViews();
    }

    public void onBUS_StationClick(Marker marker) {
        Extract_BUS_Data(marker);
        TableLayout buses = getView().findViewById(R.id.buses_at_station);
        buses.removeAllViews();
    }

    //find station times using only STATION NAME returns JSONArray
    public void Extract_LRT_Data(Marker marker) {
        String getScheduleURL = "https://metromobile.000webhostapp.com/stationLookUp.php?statName=" + marker.getTitle();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getScheduleURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSONS_RH(response);
                showJSONS_PL(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void Extract_BUS_Data(@NotNull Marker marker) {
        String getScheduleURL = "https://metromobile.000webhostapp.com/bus_stationLookUp.php?statName=" + marker.getTitle();
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

    //array list for PORT LOUIS TIMES
    private void showJSONS_PL(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            //time object 1st
            JSONObject aTime = null;
            //put whole array into jsonArray
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY_PORTL);
            for (int i = 0; i < result.length(); i++) {
                aTime = result.getJSONObject(i);
                //name.add(aTime.getString(Config.LRT_STATION_NAME));
                //name.add(aTime.getString(Config.KEY_NAME));

                TableLayout scheTable = getView().findViewById(R.id.scheTable_PL);
                View inflated = LayoutInflater.from(getContext()).inflate(R.layout.time_to_inflate, scheTable, false);

                TextView tv = (TextView) inflated.findViewById(R.id.etaTimer);
                tv.setText(aTime.getString(Config.KEY_NAME));

                TextView tvName = (TextView) inflated.findViewById(R.id.etaName);
                tvName.setText(aTime.getString(Config.LRT_STATION_NAME));

                // now add to the LinearLayoutView.
                scheTable.addView(inflated);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //array list for ROSE HILL TIMES
    private void showJSONS_RH(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject aTime = null;
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY_ROSEH);
            for (int i = 0; i < result.length(); i++) {
                aTime = result.getJSONObject(i);
                //name.add(aTime.getString(Config.LRT_STATION_NAME));
                //name.add(aTime.getString(Config.KEY_NAME));

                TableLayout scheTable = getView().findViewById(R.id.scheTable_RH);
                View inflated = LayoutInflater.from(getContext()).inflate(R.layout.time_to_inflate, scheTable, false);

                TextView tv = (TextView) inflated.findViewById(R.id.etaTimer);
                tv.setText(aTime.getString(Config.KEY_NAME));

                TextView tvName = (TextView) inflated.findViewById(R.id.etaName);
                tvName.setText(aTime.getString(Config.LRT_STATION_NAME));

                // now add to the LinearLayoutView.
                scheTable.addView(inflated);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void showJSONS_BUSES(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject aTime = null;
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY_BUSES);
            String parking = null, disabled = null, bike_rack = null;

            TableLayout table_buses = stationDetailsDialog.findViewById(R.id.buses_at_station_new);
            TableLayout table_facilities = stationDetailsDialog.findViewById(R.id.facilities_at_station);

            View inflated_buses = LayoutInflater.from(getContext()).inflate(R.layout.bus_to_inflate, table_buses, false);
            View inflated_facilities = LayoutInflater.from(getContext()).inflate(R.layout.facility_to_inflate, table_facilities, false);

            table_buses.addView(inflated_buses);
            table_facilities.addView(inflated_facilities);

            FlexboxLayout flexboxBuses = table_buses.findViewById(R.id.flexboxBuses);
            FlexboxLayout flexboxFacilities = table_facilities.findViewById(R.id.flexboxFacilities);
            flexboxBuses.removeAllViews();
            flexboxFacilities.removeAllViews();

            //show buses at station
            for (int i = 0; i < result.length(); i++) {
                aTime = result.getJSONObject(i);
                parking = aTime.getString(Config.BUS_PARKING);
                disabled = aTime.getString(Config.BUS_DISABLED);
                bike_rack = aTime.getString(Config.BUS_BIKES);

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
                   String clickedBus = String.valueOf(tvName.getText());
                    for (int j = 0; j < allBuses.size(); j++) {
                        Bus thisBus = allBuses.get(j);

                        if (clickedBus.equals(String.valueOf(thisBus.getName()))) {
                            //remove previous line before showing new one
                            if (route != null)
                                removeBUSPoly();
                            bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                            bottomSheetBehavior_BUS_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                            ShowRouteStations(thisBus);
                            new Bus().busRoute(thisBus);
                            stationDetailsDialog.dismiss();
                            bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        }
                    }
                });
            }

            //SnackBar tip component
            Snackbar.make(table_buses, "Click a bus too see its route!",
                    Snackbar.LENGTH_SHORT)
                    .show();

            //show parking availability at station
            if (parking.equals("1")) {
                ImageView parkingView = new ImageView(flexboxFacilities.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.height = 120;
                params.width = 120;
                params.setMargins(10, 0, 10, 0);
                parkingView.setBackground(getResources().getDrawable(R.drawable.station_facility));
                parkingView.setImageDrawable(getResources().getDrawable(R.drawable.ic_parking));
                parkingView.setLayoutParams(params);

                // now add to the Table.
                flexboxFacilities.addView(parkingView);
            }

            //show wheelchair access at station
            if (disabled.equals("1")) {
                ImageView disabled_view = new ImageView(flexboxFacilities.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.height = 120;
                params.width = 120;
                params.setMargins(10, 0, 10, 0);
                disabled_view.setBackground(getResources().getDrawable(R.drawable.station_facility));
                disabled_view.setImageDrawable(getResources().getDrawable(R.drawable.ic_wheelchair));
                disabled_view.setPadding(5, 5, 5, 5);
                disabled_view.setLayoutParams(params);

                // now add to the Table.
                flexboxFacilities.addView(disabled_view);
            }

            //show bike rack availability at station
            if (bike_rack.equals("1")) {
                ImageView bike_rack_view = new ImageView(flexboxFacilities.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.height = 120;
                params.width = 120;
                params.setMargins(10, 0, 10, 0);
                bike_rack_view.setBackground(getResources().getDrawable(R.drawable.station_facility));
                bike_rack_view.setImageDrawable(getResources().getDrawable(R.drawable.ic_bike_facility));
                bike_rack_view.setPadding(5, 5, 5, 5);
                bike_rack_view.setLayoutParams(params);

                // now add to the Table.
                flexboxFacilities.addView(bike_rack_view);
            }

            if (parking.equals("0") && disabled.equals("0") && bike_rack.equals("0")){
                TextView no_facilities = new TextView(flexboxFacilities.getContext());
                no_facilities.setText(R.string.no_facilities);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10,10,10,10);
                no_facilities.setTextColor(Color.BLACK);
                no_facilities.setTextSize(20);
                no_facilities.setGravity(Gravity.CENTER);
                no_facilities.setLayoutParams(layoutParams);

                // now add to the Table.
                flexboxFacilities.addView(no_facilities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    //add LRT station markers to map
    private void addRailStations() {
        allLRTMarkers = new ArrayList<>();
        allLRTStations = new ArrayList<>();

        //custom marker size
        int height = 235;
        int width = 200;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.lrt_icon_pin);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        for (int i = 0; i < setLrtStations().size(); i++) {
            Station station = setLrtStations().get(i);
            if (station != null) {
                //get LatLng for each marker in ArrayList
                lrtMarker = gMap.addMarker(new MarkerOptions()
                        .position(station.position)
                        .icon(smallMarkerIcon)
                        .title(station.name)
                        .zIndex(5.0f)
                );
                lrtMarker.setTag("LRT");
                allLRTMarkers.add(lrtMarker);
                allLRTStations.add(station);
            } else
                Log.e("its", null);
        }
    }

    //TODO: CHANGE IMPLEMENTATION OF LRT STATIONS
    private ArrayList<Station> setLrtStations () {
        ArrayList<Station> stations = new ArrayList<>();
        Station RoseHill = new Station().setStations("Rose Hill Central", "LRT", new LatLng(-20.2421818, 57.4758875));
        Station Vander = new Station().setStations("Vandersmeech", "LRT", new LatLng(-20.2354926, 57.473157));
        Station BeauB = new Station().setStations("Beau Bassin", "LRT", new LatLng(-20.2266891, 57.4673957));
        Station Barkly = new Station().setStations("Barkly", "LRT", new LatLng(-20.2209104, 57.4584639));
        Station Coromandel = new Station().setStations("Coromandel", "LRT", new LatLng(-20.1837264, 57.4693912));
        Station StLouis = new Station().setStations("St Louis", "LRT", new LatLng(-20.180942, 57.4767888));
        Station PortLouis = new Station().setStations("Port Louis Victoria", "LRT", new LatLng(-20.1625125, 57.4982089));
        stations.add(RoseHill);
        stations.add(Vander);
        stations.add(BeauB);
        stations.add(Barkly);
        stations.add(Coromandel);
        stations.add(StLouis);
        stations.add(PortLouis);
        return stations;
    }

    private void setMapPadding (Float offset) {
        //From 0.0 (min) - 1.0 (max)
        // bsExpanded - bsCollapsed;
        Float maxMapPaddingBottom = 1.0f;
        gMap.setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom));
    }

    //bus station markers
    private void addBusStations () {
        allBusMarkers = new ArrayList<>();
        allBusStations = new ArrayList<>();
        //custom marker size
        int height = 235;
        int width = 200;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bus_pin_new);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        for (int i = 0; i < setBusStations().size(); i++) {
            Station station = setBusStations().get(i);
            //get LatLng for each marker in ArrayList
            busMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(station.position.latitude, station.position.longitude))
                    .icon(smallMarkerIcon)
                    .title(station.name)
                    .zIndex(5.0f)
            );
            busMarker.setTag("BUS");
            allBusMarkers.add(busMarker);
            allBusStations.add(station);
        }
    }

    private ArrayList<Station> setBusStations () {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(new Reduit().addStation());
        stations.add(new Kennedy().addStation());
        stations.add(new QB().addStation());
        stations.add(new St_John().addStation());
        stations.add(new Pailles().addStation());
        stations.add(new Moka().addStation());
        stations.add(new Bagatelle().addStation());
        stations.add(new Victoria().addStation());
        stations.add(new BeauBassin().addStation());

        return stations;
    }

    private ArrayList<Bus> addBuses () {
        allBuses = new ArrayList<>();
        Bus bus_153 = new Bus_153();
        Bus bus_163 = new Bus_163();
        Bus bus_3 = new Bus_3();
        allBuses.add(bus_153);
        allBuses.add(bus_163);
        allBuses.add(bus_3);

        return allBuses;
    }

    private void walkToStation () {
        for (int i = 0; i < setBusStations().size(); i++) {
            Station station = setBusStations().get(i);

            if (userUpdates.location != null) {
                curLocation = new LatLng(userUpdates.location.getLatitude(), userUpdates.location.getLongitude());
                distance = (int) SphericalUtil.computeDistanceBetween(curLocation, station.getPosition());
                station.setDistance(distance);

                if (distance < 1000) {

                    if (station.type.equals("BUS")) {
                        String origin = String.valueOf(userUpdates.location.getLatitude()) + "," + String.valueOf(userUpdates.location.getLongitude());
                        String destination = String.valueOf(station.position.latitude) + "," + String.valueOf(station.position.longitude);



                        DirectionsResult results = getWalkingDetails(origin, destination, TravelMode.WALKING);
                        if (results != null) {
                            addWalkToBusPolyline(results, gMap);
                            positionCamera(results.routes[overview], gMap);
                        }else
                            Log.e("it is ", "null");
                    }
                }
            }
        }
    }

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        //sendNotif("LRTMate App", String.format("%s entered the station.", dataSnapshot.getKey()));
        LatLng loc = new LatLng(location.latitude, location.longitude);
        if (location != null) {
            if (!loc.equals(curLocation)) {
                //Log.e("entered new", dataSnapshot.getKey());
                entered_Station(location);
            } else {
                Log.e("entered", "same");
                //entered_Station(location);
            }
        }
    }

    @Override
    public void onDataExited(DataSnapshot dataSnapshot) {
        Log.e("exited", dataSnapshot.getKey());
        //sendNotif("LRTMate App", String.format("%s left the station.", dataSnapshot.getKey()));
        exited_Station();
    }

    @Override
    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
        Log.e("moving", dataSnapshot.getKey());
        //sendNotif("LRTMate App", String.format("%s is moving around the station.", dataSnapshot.getKey()));
    }

    private void sendNotif(String title, String message) {
        String NOTIFICATION_CHANNEL_ID = App.CHANNEL_ID;
        NotificationManager manager = (NotificationManager) this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Notifs",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("Desccription");
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.track_location)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_top_track_location));

        Notification notification = builder.build();
        manager.notify(new Random().nextInt(), notification);
    }

    private void exited_Station () {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firebaseFirestore.collection("stationdetails");

        userUpdates.setNearest_station(null);
        userUpdates.setDistance_to_nearest_station(0);

        try {
            decrementCounter(reference, UserUpdates.nearest_station.name);
            Log.e("user left", UserUpdates.nearest_station.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //pinpoint the user when entering a trigger
    private void entered_Station (GeoLocation location) {
        _approaching = new ArrayList<>();
        boolean found = false;
        LatLng user = new LatLng(location.latitude, location.longitude);
        Station station = new Station();

        while (found == false) {
            for (StationFence stationFence : getStationsFences()) {
                String newStation;
                int nearest_station = (int) SphericalUtil.computeDistanceBetween(user, stationFence.stationLocation);
                //if user becomes <30m away from a station
                if (nearest_station <= Config.ALLOWED_PROXIMITY) {
                    int dist_to = nearest_station;

                    if (!foundfences.contains(stationFence)) {
                        foundfences.add(stationFence);
                    }

                    if ((dist_to > Config.ACCURACY_DISTANCE)) {
                        //Log.e("entered", "poss");
                        //if they are close enough <50m
                        station.name = (stationFence.stationName);
                        station.type = (stationFence.type);
                        station.position = (stationFence.stationLocation);
                        station.distance = (nearest_station);
                        userUpdates.setNearest_station(station);
                        userUpdates.setDistance_to_nearest_station(dist_to);
                        UserUpdates.distance_to_nearest_station = dist_to;
                        //Log.e("set poss", "station");

                        if (stationFence.stationName.equals(station.name)) {
                            newStation = station.name;
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            CollectionReference reference = firebaseFirestore.collection("stationdetails");
                            incrementCounter(reference, newStation);
                            //Log.e("poss dist to", station.name + " " + dist_to);
                            found = true;
                            break;
                        }
                        found = true;
                        break;
                    }
                    //if user becomes <25m away from a station
                    else if (dist_to <= Config.ACCURACY_DISTANCE) {
                        //Log.e("entered", "closer");
                        station.name = (stationFence.stationName);
                        station.type = (stationFence.type);
                        station.position = (stationFence.stationLocation);
                        station.distance = (nearest_station);
                        userUpdates.setNearest_station(station);
                        userUpdates.setDistance_to_nearest_station(dist_to);
                        UserUpdates.distance_to_nearest_station = dist_to;
                        //Log.e("set", "station");

                        if (stationFence.stationName.equals(station.name)) {
                            newStation = station.name;
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            CollectionReference reference = firebaseFirestore.collection("stationdetails");
                            incrementCounter(reference, newStation);
                            //Log.e("accu dist to", station.name + " " + dist_to);
                            found = true;
                            break;
                        }
                        found = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

    }

    public Task<Void> incrementCounter(final CollectionReference ref, String name) {
        DocumentReference occRef = ref.document("occupancy");
        if (UserUpdates.nearest_station.name != null) {
            if (!name.equals(UserUpdates.nearest_station.name)) {
                if (UserUpdates.updatedOcc == false) {
                    occRef.update(name, FieldValue.increment(1));
                    UserUpdates.updatedOcc = true;
                }
            }
        }
        getCount(occRef, name);
        return null;
    }

    public Task<Integer> getCount(final DocumentReference ref, String stationName) {
        // Sum the count of each shard in the subcollection
        //DocumentReference doc = ref.collection("stationdetails").document("occupancy");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int ran = 1;
                    int run = 2;
                    while (ran < run) {
                        HomeFragment.initOccupancy();
                        Log.e("now occupied by", String.valueOf(UserUpdates.cur_stat_occupancy));
                        ran++;
                    }
                } else {
                    Log.e(TAG, "No such document");
                }
            } else {
                Log.e(TAG, "get failed with ", task.getException());
            }
        });

        ref.addSnapshotListener((snapshot, error) -> {
            UserUpdates.cur_stat_occupancy = snapshot.getDouble(stationName);
            if (UserUpdates.tracking == true) {
                int ran = 1;
                int run = 2;
                while (ran < run) {
                    HomeFragment.initOccupancy();
                    Log.e("now occupied by", String.valueOf(UserUpdates.cur_stat_occupancy));
                    ran++;
                }
            }
        });
        return null;
    }

    public Task<Void> decrementCounter (final CollectionReference ref, String name) {
        DocumentReference occRef = ref.document("occupancy");

        occRef.update(name, FieldValue.increment(-1));
        getCount(occRef, name);
        return null;
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText(getContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}