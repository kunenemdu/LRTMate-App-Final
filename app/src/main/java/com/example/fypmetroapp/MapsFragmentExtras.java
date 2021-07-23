package com.example.fypmetroapp;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Distance;
import com.google.maps.model.TravelMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.tomtom.online.sdk.routing.RoutingApi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.green;

public class MapsFragmentExtras extends Fragment implements LocationListener {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    //Variable Declarations
    public static GoogleMap gMap;
    public static GoogleMap map_displaying_extras;
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker lrtMarker;
    private Marker busMarker;
    Marker bus_location;
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
    LatLng curLocationLatLng;
    Location curLocation;
    TextView textAddress;
    static LinearLayout llFollow_Route, llBottomSheet;
    LinearLayout rlDirections;
    private Polyline polyline_LRT;
    private Polyline polyline_BUS;
    private Polyline polyline_Directions;
    static BottomSheetBehavior<LinearLayout> bottomSheetBehavior_Follow_Route,
            bottomSheetBehavior_Directions;
    static CardView fab_start, fab_switch;
    LocationCallback locationCallback;
    ArrayList<Marker> allLRTMarkers;
    ArrayList<Marker> allBusMarkers;
    static ArrayList<Marker> allMarkers;
    static ArrayList<Station> allBusStations = new ArrayList<>(),
            allLRTStations = new ArrayList<>(),
            allLRTStations_reversed = new ArrayList<>(),
            allStations = new ArrayList<>();
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    DatabaseReference userRef;
    DatabaseReference driverRef;
    TextView timeText;
    TextView directionText;
    TextView busText, busAtStation;
    TextView clickedBus;
    ImageButton open_Dir, fav_to, fav_from, selectLoc, selectReverse;
    public static final int overview = 0;
    static CardView lrt_mode, close_routes, close_Dir;
    ArrayList<Bus> allBuses;
    int distance = 0;
    GeoFire userGeoFire;
    GeoFire driverGeoFire;
    public static ArrayList<Polyline> route = new ArrayList<>();
    SharedPreferences preferences;
    LocationRequest locationRequest;
    Marker userMarker;
    Marker driverMarker;
    private Dialog stationDetailsDialog;
    static Dialog ride_stops_dialog;
    GlobalProperties properties = new GlobalProperties();
    FirebaseAuth firebaseAuth;
    public static String uid;
    public static String driverID;
    public static List<StationFence> stationsFences;
    ArrayList<Station> _approaching;
    ArrayList<StationFence> foundfences;
    private LocationManager locationManager;
    private String provider;
    ImageView occupancy_anim;
    static ImageView follow_anim;
    SupportMapFragment supportMapFragment;
    private ClusterManager<MarkerClusterItem> clusterManager;
    MarkerClusterRenderer<MarkerClusterItem> clusterRenderer;
    RoutingApi onlineRoutingApi;
    ArrayList<Map<String, String>> favourites;
    Set<String> favourite;
    MaterialSearchBar materialSearchBar;
    PlacesClient placesClient;
    List<AutocompletePrediction> predlist;
    ArrayList<Marker> markers = new ArrayList<>();
    EditText edtText;
    EditText inputText;
    ProgressDialog progressDialog;
    static FrameLayout computed_layout;
    private TinyDB tinyDB = NavigationActivity.tinyDB;
    ArrayList<String> walking_Origin_instructions;
    ArrayList<String> walking_Dest_instructions;
    double bus_int;
    static RecyclerView recyclerView_lrt, recyclerView_alts, recyclerView_full;
    public static Route previous;
    String role;
    CardView back_to_full_maps;
    LinearLayout following_route;
    final static ArrayList<Route> routes = new ArrayList<>();
    static ArrayList<Object> routes_objects = new ArrayList<>();
    static FragmentManager fragmentManager;
    boolean done_or = false;
    static Marker origin_marker, destination_marker;
    Polyline journey_polyline;
    GeofencingClient geofencingClient;
    static GeofencingClient geofencingClient_riding;
    ArrayList<Geofence> geofenceList;
    static ArrayList<Geofence> geofenceList_riding;
    PendingIntent geofencePendingIntent;
    static PendingIntent geofencePendingIntent_riding;
    ArrayList<String> full_instructions_inter = new ArrayList<>();
    ArrayList<String> full_summary_inter = new ArrayList<>();
    ArrayList<Station> full_station_list_inter = new ArrayList<>();
    DirectionsResult result_o_inter, result_d_inter;
    int route_length_inter = 0, route_duration_inter = 0;

    //Class Declarations
    static Origin origin = new Origin();
    static Destination destination = new Destination();
    UserUpdates userUpdates = new UserUpdates();
    SearchPlace searchPlace = new SearchPlace();
    Route direct_bus_route = null;
    Route connect_bus_route = null;
    Route direct_lrt_route = null;
    Route intermodal = null;
    Route intermodal_alt = null;
    static Journey journey = new Journey();
    static Route picked_route = new Route();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps_extras, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_extras);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        Log.e("created", "extras");
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofencingClient_riding = LocationServices.getGeofencingClient(getActivity());
        back_to_full_maps = getView().findViewById(R.id.back_to_maps_full);
        fab_start = getView().findViewById(R.id.start_route);
        fab_switch = getView().findViewById(R.id.switch_route);
        fav_from = getView().findViewById(R.id.fav_from);
        fav_to = getView().findViewById(R.id.fav_to);
        lrt_mode = getView().findViewById(R.id.lrtMode);
        close_Dir = getView().findViewById(R.id.close_dir);
        selectLoc = getView().findViewById(R.id.selectLocation);
        selectReverse = getView().findViewById(R.id.selectReverse);
        computed_layout = getView().findViewById(R.id.computed_routes_frag);
        close_routes = getView().findViewById(R.id.close_routes);
        recyclerView_alts = computed_layout.findViewById(R.id.alt_routes_recycler);
        recyclerView_lrt = computed_layout.findViewById(R.id.best_route_recycler);
        recyclerView_full = getView().findViewById(R.id.route_recycler);
        rlDirections = getView().findViewById(R.id.directions_frag);
        open_Dir = getView().findViewById(R.id.startDirections_extras);

        fab_start.setOnClickListener(Buttons);
        fab_switch.setOnClickListener(Buttons);
        lrt_mode.setOnClickListener(Buttons);
        fav_from.setOnClickListener(Buttons);
        fav_to.setOnClickListener(Buttons);
        selectReverse.setOnClickListener(Buttons);
        selectLoc.setOnClickListener(Buttons);
        close_Dir.setOnClickListener(Buttons);
        back_to_full_maps.setOnClickListener(Buttons);
        open_Dir.setOnClickListener(Buttons);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.map_extras);
                        mapFragment.getMapAsync(MapsFragmentExtras.this::onMapReady);
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

        initAutoFrags();
        initFollowRoute_Component();
    }

    private void GeoFireConfig() {
        userRef = FirebaseDatabase.getInstance().getReference("User");
        driverRef = FirebaseDatabase.getInstance().getReference("tracking");
        userGeoFire = new GeoFire(userRef);
        driverGeoFire = new GeoFire(driverRef);
    }

    @SuppressLint({"ResourceType", "NewApi"})
    public void onMapReady(GoogleMap googleMap) {
        //initialise map for use
        gMap = googleMap;
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

        gMap.setOnMarkerClickListener(this::onMarkerClick);

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
        allMarkers = new ArrayList<>();
        role = tinyDB.getString("role");

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Marker clicked = gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(latLng.toString()));

                clicked.setTag("P");
            }
        });

        //TODO: MAP CLICK LISTENER
        gMap.setOnMapClickListener(latLng -> {
            removeDirectionsPoly();
            //hide fabD button and sheet
        });
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterRenderer = new MarkerClusterRenderer<>(getContext(), googleMap, clusterManager);
        allStations = new ArrayList<>();
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
                    curLocationLatLng = loc1;

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

    public void removeDirectionsPoly() {
        //remove whole bus POLYLINE
        if (route != null) {
            for (Polyline line : route) {
                line.remove();
            }
            route.clear();
        }
    }

    //event listener for buttons
    android.view.View.OnClickListener Buttons = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.back_to_maps_full:
                    NearMeHolder.maps_flipper.setDisplayedChild(0);
                    break;
                case R.id.fav_to:
                    favourite_to();
                    break;
                case R.id.fav_from:
                    favourite_from();
                    break;
                case R.id.close_dir:
                    rlDirections.setVisibility(View.GONE);
                    break;
                case R.id.lrtMode:
                    checkParams();
                    break;
                case R.id.selectLocation:
                    selectLoc();
                    break;
                case R.id.selectReverse:
                    selectReverse();
                    break;
                case R.id.close_routes:
                    computed_layout.setVisibility(View.INVISIBLE);
                    break;
                case R.id.start_route:
                    startRouting(getContext(), origin_marker);
                    break;
                case R.id.startDirections_extras:
                    rlDirections.setVisibility(View.VISIBLE);
                    break;
                case R.id.switch_route:
                    swap();
                    break;
            }
        }
    };

    private void swap () {
        bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_COLLAPSED);
        computed_layout.setVisibility(View.VISIBLE);
    }

    static void startRouting(Context context, Marker start) {
        //following_route.setVisibility(View.VISIBLE);
        initRoute_Fences(previous.getRidingStations(), context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Open Google Maps?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //String latitude_d = String.valueOf(next.getPosition().latitude);
                        //String longitude_d = String.valueOf(next.getPosition().longitude);
                        String latitude_o = String.valueOf(start.getPosition().latitude);
                        String longitude_o = String.valueOf(start.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude_o + "," + longitude_o);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        try {
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                sendNotification("Don't Worry. I'm Still Running!",
                                        "I will check back with you when you arrive at the station.", context);
                                context.startActivity(mapIntent);
                            }
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                            Toast.makeText(context, "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean onMarkerClick(Marker marker) {
        try {
            if (marker.getTag().equals("P")) {
                titleText.setText(marker.getTitle());
                textAddress.setText(marker.getPosition().toString());
                //show it as collapsed
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_EXPANDED);
                //hide all the other bottom sheets
                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
            else if (marker.getTag().equals("D")) marker.showInfoWindow();
            else if (marker.getTag().equals("O")) marker.showInfoWindow();
            else if (marker.getTag().equals("M")) marker.showInfoWindow();

        } catch (Exception e) {
        }
        return true;
    }

    private void selectLoc () {
        String userOrigin = userUpdates.getLatLngLocation().latitude + "," + userUpdates.getLatLngLocation().longitude;
        Location orig = userUpdates.getLocation();
        edtText.setText("My Location");
        origin.setOrigin(userOrigin);
    }

    private void selectReverse () {
        String oradd = origin.getAddress();
        com.example.fypmetroapp.LatLng orpos = origin.getPosition();
        String orname = origin.getName();
        String or = origin.getOrigin();
        String desadd = destination.getAddress();
        String desname = destination.getName();
        String des = destination.getDestination();
        com.example.fypmetroapp.LatLng despos = destination.getPosition();

        origin.setOrigin(des);
        origin.setName(desname);
        origin.setPosition(despos);
        origin.setAddress(desadd);
        destination.setDestination(or);
        destination.setPosition(orpos);
        destination.setAddress(oradd);
        destination.setName(orname);

        edtText.setText(desname);
        inputText.setText(orname);
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
        autocompleteFragmentDestination.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragmentOrigin.setCountry("MU");
        autocompleteFragmentOrigin.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragmentDestination.setHint("Where are you going?");
        autocompleteFragmentOrigin.setHint("Where are you starting?");
        EditText edtText = autocompleteFragmentOrigin.getView().findViewById(R.id.places_autocomplete_search_input);
        edtText.setTextSize(14.0f);
        EditText inputText = autocompleteFragmentDestination.getView().findViewById(R.id.places_autocomplete_search_input);
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
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                com.example.fypmetroapp.LatLng latLng = new com.example.fypmetroapp.LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                String user_Destination = temp.getLatitude() + "," + temp.getLongitude();
                destination.setDestination(user_Destination);
                destination.setAddress(place.getAddress());
                destination.setName(place.getName());
                destination.setPosition(latLng);
                Marker destination;

                destination = gMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName())
                );
                destination.setTag("D");
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
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                com.example.fypmetroapp.LatLng latLng = new com.example.fypmetroapp.LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                String user_origin = temp.getLatitude() + "," + temp.getLongitude();
                Log.e("mine", latLng.latitude + "," + latLng.longitude);
                Log.e("google", user_origin + "");
                origin.setPosition(latLng);
                origin.setOrigin(user_origin);
                origin.setName(place.getName());
                origin.setAddress(place.getAddress());

                Marker origin;

                origin = gMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName())
                );
                origin.setTag("O");
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });
    }

    private void initFollowRoute_Component() {
        // get the bottom sheet view
        llFollow_Route = getView().findViewById(R.id.follow_route);

        // init the bottom sheet behavior
        bottomSheetBehavior_Follow_Route = BottomSheetBehavior.from(llFollow_Route);

        // change the state of the bottom sheet
        bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set callback for changes
        bottomSheetBehavior_Follow_Route.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_Follow_Route.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
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

    public Polyline getJourney_polyline() {
        return journey_polyline;
    }

    public void setJourney_polyline(Polyline journey_polyline) {
        this.journey_polyline = journey_polyline;
    }

    @SuppressLint("NewApi")
    private void favourite_to () {
        Favourite favourite = new Favourite(destination.getDestination(), destination.getName(), destination.getAddress(), destination.getPosition());
        NavigationActivity.tinyDB.putObject("fav1", favourite);
    }

    @SuppressLint("NewApi")
    private void favourite_from () {
        Favourite favourite = new Favourite(origin.getOrigin(), origin.getName(), origin.getAddress(), origin.getPosition());
        NavigationActivity.tinyDB.putObject("fav2", favourite);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkParams () {
        allBuses = Maps_Full_Access.allBuses;
        allBusStations = Maps_Full_Access.setBusStations();
        allLRTStations = Maps_Full_Access.setLrtStations();
        allLRTStations_reversed = Maps_Full_Access.allLRTStations_reversed;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Finding You The Best Route...");
        progressDialog.show();
        rlDirections.setVisibility(View.INVISIBLE);
        walking_Origin_instructions = new ArrayList<>();
        walking_Dest_instructions = new ArrayList<>();

        //check if there are any bus stops at destination
        Station checkDest_BUS = anyBusStopsAtDest(new LatLng(destination.getPosition().latitude, destination.getPosition().longitude));
        Station checkOri_BUS = anyBusStopsAtOri(new LatLng(origin.getPosition().latitude, origin.getPosition().longitude));
        Station checkOri_LRT = anyLRTStopsAtOri(new LatLng(origin.getPosition().latitude, origin.getPosition().longitude));
        Station checkDest_LRT = anyLRTStopsAtDest(new LatLng(destination.getPosition().latitude, destination.getPosition().longitude));

        if (checkOri_LRT != null && checkDest_LRT != null) {
            if (checkDest_BUS != null && checkOri_BUS != null) {
                findRoute_BUS(checkOri_BUS, checkDest_BUS);
                direct_route_intermodal(checkOri_BUS, checkDest_BUS);
                findRoute_LRT(checkOri_LRT, checkDest_LRT);

                RouteInstructs();
            }
        }
    }

    @SuppressLint({"NewApi", "LongLogTag"})
    private boolean direct_route_intermodal (Station ori, Station dest) {
        boolean found_first = false;
        Station dest_lrt = null, ori_lrt = null;
        int max_or = Integer.MAX_VALUE;
        for (Bus bus_ori: ori.getBuses()) {
            for (Station station: allLRTStations) {
                distance = (int) SphericalUtil.computeDistanceBetween(station.position, ori.position);

                if (distance < max_or) {
                    max_or = distance;
                    ori_lrt = station;
                }
            }
        }
        int max_de = Integer.MAX_VALUE;
        for (Bus bus_dest: ori.getBuses()) {
            for (Station station: allLRTStations) {
                distance = (int) SphericalUtil.computeDistanceBetween(station.position, dest.position);

                if (distance < max_de) {
                    max_de = distance;
                    dest_lrt = station;
                }
            }
        }

        if (ori_lrt != null)
            if (dest_lrt != null) {
                Station closest_o = new Station();
                Station closest_d = new Station();
                Bus ori_bus = new Bus();
                Bus dest_bus = new Bus();
                double max_o = Integer.MAX_VALUE;
                double max_d = Integer.MAX_VALUE;
                for (Bus bus_or: ori.getBuses()) {
                    for (Station station: bus_or.getStops()) {
                        double distance = SphericalUtil.computeDistanceBetween(ori_lrt.position, station.position);
                        if (distance < max_o) {
                            max_o = distance;
                            closest_o = station;
                            ori_bus = bus_or;
                        }
                    }
                }
                for (Bus bus_de: dest.getBuses()) {
                    for (Station station: bus_de.getStops()) {
                        double distance = SphericalUtil.computeDistanceBetween(dest_lrt.position, station.position);
                        if (distance < max_d) {
                            max_d = distance;
                            closest_d = station;
                            dest_bus = bus_de;
                        }
                    }
                }
                if (!closest_o.name.equals(null)) {
                    if (ori_bus != null)
                        if (closest_d.name != null)
                            if (dest_bus != null) {
                                if (!found_first) {
                                    boolean simple = false;
                                    while (!simple) {
                                        intermodal = new Route();
                                        simple = simple_intermodal_bus_lrt(ori_lrt, dest_lrt);
                                        if (simple) {
                                            ArrayList<String> ins = new ArrayList<>();
                                            ArrayList<String> ins_sum = new ArrayList<>();
                                            ins.add("----------------- MY DIRECTIONS -----------------");
                                            if (!closest_o.name.equals(ori.name)) {
                                                ins_sum.add(" 🚶 ➡ ‍");
                                                ins_sum.add(ori_bus.getName() + " ➡ ‍");
                                                ins_sum.add(" 🚶 ➡ ‍‍");
                                                ins.add("Should take the Bus: " + ori_bus.getName() + " from " + ori.getName());
                                                ins.add("Get off at " + closest_o.getName() + ". Then walk to " + ori_lrt.getName());
                                            }
                                            else {
                                                ins.add("Walk to " + ori_lrt.name);
                                                ins_sum.add(" 🚶 ➡ ");
                                            }
                                            ins.addAll(full_instructions_inter);
                                            ins_sum.addAll(full_summary_inter);
                                            intermodal.setSummary(ins_sum);
                                            intermodal.setInstructions(ins);
                                            if (!closest_d.name.equals(dest.name)) {
                                                ins_sum.add("" + dest_bus.getName());
                                                ins_sum.add(" ➡ 🚶 ");
                                                ins.add("From " + dest_lrt.getName() + " Walk to " + closest_d.getName());
                                                ins.add("Then take the Bus: " + dest_bus.getName() + " from " + closest_d.getName());
                                            }
                                            else {
                                                ins.add("Walk to " + destination.name);
                                                ins_sum.add(" 🚶 ");
                                            }
                                            ins.add("----------------- MY DIRECTIONS -----------------");
                                            intermodal.setInstructions(ins);
                                            intermodal.setOrigin_walking(result_o_inter);
                                            intermodal.setDestination_walking(result_d_inter);
                                            intermodal.setInstructions(ins);
                                            intermodal.setTotal_route_duration(route_duration_inter);
                                            intermodal.setRidingStations(full_station_list_inter);
                                            intermodal.setOrigin(ori);
                                            intermodal.setDestination(ori_lrt);
                                            intermodal.setTotal_route_length(route_length_inter);
                                            found_first = true;
                                        }
                                    }
                                }
                                if (found_first) {
                                    boolean simple_ = false;
                                    while (!simple_) {
                                        intermodal_alt = new Route();
                                        simple_ = simple_intermodal_bus_lrt(ori_lrt, dest_lrt);
                                        if (simple_) {
                                            ArrayList<String> ins = new ArrayList<>();
                                            ArrayList<String> ins_sum = new ArrayList<>();
                                            ins.add("----------------- MY DIRECTIONS -----------------");
                                            if (!closest_o.name.equals(ori.name)) {
                                                ins_sum.add(" 🚶 ➡ ‍");
                                                ins_sum.add(ori_bus.getName() + " ➡ ‍");
                                                ins_sum.add(" 🚶 ➡ ‍‍");
                                                ins.add("Should take the Bus: " + ori_bus.getName() + " from " + ori.getName());
                                                ins.add("Get off at " + closest_o.getName() + ". Then walk to " + ori_lrt.getName());
                                            }
                                            else {
                                                ins.add("Walk to " + ori_lrt.name);
                                                ins_sum.add(" 🚶 ➡ ");
                                            }
                                            ins.addAll(full_instructions_inter);
                                            ins_sum.addAll(full_summary_inter);
                                            intermodal_alt.setSummary(ins_sum);
                                            intermodal_alt.setInstructions(ins);
                                            if (!closest_d.name.equals(dest.name)) {
                                                ins_sum.add("" + dest_bus.getName());
                                                ins_sum.add(" ➡ 🚶 ");
                                                ins.add("From " + dest_lrt.getName() + " Walk to " + closest_d.getName());
                                                ins.add("Then take the Bus: " + dest_bus.getName() + " from " + closest_d.getName());
                                            }
                                            else {
                                                ins.add("Walk to " + destination.name);
                                                ins_sum.add(" 🚶 ");
                                            }
                                            ins.add("----------------- MY DIRECTIONS -----------------");
                                            intermodal_alt.setOrigin_walking(result_o_inter);
                                            intermodal_alt.setDestination_walking(result_d_inter);
                                            intermodal_alt.setInstructions(ins);
                                            intermodal_alt.setTotal_route_duration(route_duration_inter);
                                            intermodal_alt.setRidingStations(full_station_list_inter);
                                            intermodal_alt.setOrigin(ori);
                                            intermodal_alt.setDestination(ori_lrt);
                                            intermodal_alt.setTotal_route_length(route_length_inter);
                                            return found_first;
                                        }
                                    }
                                }
                            }
                }
            }
        return found_first;
    }

    private Location create (LatLng latLng) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLongitude(latLng.longitude);
        location.setLatitude(latLng.latitude);
        return location;
    }

    //returns station_bus closest to the destination city
    private int closestDestDistance_BUS (LatLng destiL) {
        int small = Integer.MAX_VALUE;
        for (Station station: allBusStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(destiL, station.getPosition());
            if (distance < small) {
                small = distance;
            }
        }
        return small;
    }

    //returns station_bus closest to the origin city
    private int closestOriDistance_BUS(LatLng oriL) {
        int small = Integer.MAX_VALUE;
        for (Station station: allBusStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(oriL, station.getPosition());
            if (distance < small) {
                small = distance;
            }
        }
        return small;
    }

    //returns bus station_bus closest to the destination city
    private Station anyBusStopsAtDest (LatLng destiL) {
        Station dest = new Station();
        for (Station station: allBusStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(destiL, station.getPosition());
            if (distance == closestDestDistance_BUS(destiL)) {
                Log.e("found dest BUS stat", station.getName());
                destination.setStation_bus(station);
                dest = station;
            }
        }
        return dest;
    }

    //returns bus station_bus closest to the origin city
    private Station anyBusStopsAtOri (LatLng oriL) {
        Station ori = new Station();
        for (Station station: allBusStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(oriL, station.getPosition());
            if (distance == closestOriDistance_BUS(oriL)) {
                Log.e("found ori BUS stat", station.getName());
                origin.setStation_bus(station);
                ori = station;
            }
        }
        return ori;
    }

    //returns bus station_bus closest to the origin city
    private Station anyLRTStopsAtOri (LatLng oriL) {
        Station ori = new Station();
        for (Station station: allLRTStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(oriL, station.getPosition());
            if (distance == closestOriDistance_LRT(oriL)) {
                Log.e("found ori LRT stat", station.getName());
                origin.setStation_lrt(station);
                ori = station;
            }
        }
        return ori;
    }

    //returns bus station_bus closest to the destination city
    private Station anyLRTStopsAtDest (LatLng destiL) {
        Station dest = new Station();
        for (Station station: allLRTStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(destiL, station.getPosition());
            if (distance == closestDestDistance_LRT(destiL)) {
                Log.e("found dest LRT stat", station.getName());
                destination.setStation_lrt(station);
                dest = station;
            }
        }
        return dest;
    }

    //returns station_lrt closest to the origin city
    private int closestOriDistance_LRT (LatLng oriL) {
        int small = Integer.MAX_VALUE;
        for (Station station: allLRTStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(oriL, station.getPosition());
            if (distance < small) {
                small = distance;
            }
        }
        return small;
    }

    //returns station_lrt closest to the destination city
    private int closestDestDistance_LRT (LatLng destiL) {
        int small = Integer.MAX_VALUE;
        for (Station station: allLRTStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(destiL, station.getPosition());
            if (distance < small) {
                small = distance;
            }
        }
        return small;
    }

    private static DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .await();
        } catch (ApiException | IOException | InterruptedException e) {
            Log.e("Directions", e.getMessage());
            return null;
        }
    }

    public static GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setApiKey(Config.MYAPI_KEY)
                .setConnectTimeout(0, TimeUnit.SECONDS)
                .setReadTimeout(0, TimeUnit.SECONDS)
                .setWriteTimeout(0, TimeUnit.SECONDS);
    }

    @SuppressLint("NewApi")
    private boolean findRoute_LRT(Station ori, Station dest) {
        try {
            boolean direct = direct_LRT(ori, dest);
            if (!direct) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Sorry! No Direct LRT Routes from "
                        + ori.getName() + " to " + dest.getName(), Toast.LENGTH_LONG).show();
                return false;
            }
            else if (direct) {
                return true;
            }
            else {
                Toast.makeText(getContext(), "Failed To Find Any LRT Station Starting Point!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch (Exception e) {
            Toast.makeText(getContext(), e + " find lrt Sorry! No Possible LRT Routes from "
                    + ori.getName() + " to " + dest.getName(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @SuppressLint("NewApi")
    private boolean findRoute_BUS (Station ori, Station dest) {
        try {
            boolean direct = direct_route_bus1(ori, dest);
            boolean conn = connecting_route(ori, dest);

            if (!direct) {
                if (!conn) {
                    Toast.makeText(getContext(), "Sorry! No Connecting Bus Routes from "
                            + ori.getName() + " to " + dest.getName(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else if (direct || conn) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            Log.e("error fbus", e.getMessage());
            //Toast.makeText(getContext(), e.getMessage() + " find bus ...", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @SuppressLint("NewApi")
    private boolean direct_route_bus1 (Station ori, Station dest) {
        boolean found = false;
        for (Bus bus_ori: ori.getBuses()) {
            for (Bus bus_dest: dest.getBuses()) {
                for (Station station: allBusStations) {
                    for (Bus here: station.getBuses()) {
                        if (here.getName() == bus_dest.getName() && here.getName() == bus_ori.getName()) {
                            ArrayList<String> full_route = new ArrayList<>();
                            ArrayList<String> full_summary = new ArrayList<>();
                            ArrayList<Station> riding_stations = new ArrayList<>();
                            direct_bus_route = new Route();

                            Map<String, Map> maps_origin = walkingFromOrigin_BUS(ori);
                            Map<String, Map> maps_dest = walkingToDestination_BUS(dest);

                            Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                            Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                            Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                            ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                            ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                            Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                            Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                            Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                            ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                            ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                            DirectionsResult result_o = hm_results_o.get("ResultValue");
                            DirectionsResult result_d = hm_results_d.get("ResultValue");
                            double duration_origin_walking = hm_values_o.get("DurationValue");
                            double duration_dest_walking = hm_values_d.get("DurationValue");
                            if (result_o != null && result_d != null) {
                                direct_bus_route.setOrigin_walking(result_o);
                                direct_bus_route.setDestination_walking(result_d);
                                double dir_dur = duration_origin_walking + duration_dest_walking;
                                double o_length = direct_bus_route.origin_walking.routes[0].legs[0].distance.inMeters;
                                double d_length = direct_bus_route.destination_walking.routes[0].legs[0].distance.inMeters;
                                int total_route_length = (int) (o_length + d_length);
                                direct_bus_route.setTotal_route_length(total_route_length);

                                boolean yes = false;

                                //if ori-stat & dest-stat share same bus
                                //user get on that bus
                                //and go to destination
                                for (String ins: instructions_o) {
                                    full_route.add(ins);
                                }
                                for (String ins: summary_o) {
                                    full_summary.add(ins);
                                }

                                full_route.add("----MY DIRECTIONS----");
                                full_route.add("Take " + here.getName() + " from " + ori.getName());
                                full_summary.add(" 🚶‍ ➡ " + here.getName() + " ➡ 🚶‍");
                                double interval = tinyDB.getDouble(String.valueOf(here.getName()));
                                int stops_to = Math.abs(getPosOrigin_BUS(here, ori) - getPosOrigin_BUS(here, dest));
                                for (Station ride: here.stops) {
                                    if (!ride.name.equals(station.name))
                                        riding_stations.add(ride);
                                }
                                full_route.add("Ride for " + stops_to + " stop(s)");
                                full_route.add("Then get off at " + station.getName());
                                full_route.add("----MY DIRECTIONS----");

                                int total_time = (int) (stops_to * interval);
                                int duration = (int) ((int) dir_dur + total_time);

                                for (String ins: instructions_d) {
                                    full_route.add(ins);
                                }
                                for (String ins: summary_d) {
                                    full_summary.add(ins);
                                }
                                direct_bus_route.setInstructions(full_route);
                                direct_bus_route.setStops_duration(total_time);
                                direct_bus_route.setTotal_route_duration(duration);
                                direct_bus_route.setSummary(full_summary);
                                Log.e("direct done", "done");
                            }
                            return found = true;
                        }
                    }
                }
            }
        }
        return found;
    }

    @SuppressLint("NewApi")
    private boolean connecting_route(Station ori, Station dest) {
        boolean done_one_con = false;
        for (Bus bus : ori.getBuses()) {
            //for bus at origin
            for (Bus bus2 : dest.getBuses()) {
                for (Station station1 : bus.getStops()) {
                    for (Station station2 : bus2.getStops()) {
                        if (station1.getName().equals(station2.getName())) {
                            for (Bus bus_switch1: station1.getBuses()) {
                                //for bus at origin
                                for (Bus bus_switch2 : dest.getBuses()) {
                                    //then for bus at destination
                                    if (bus_switch1.getName() == (bus_switch2.getName())) {
                                        //if they share same bus
                                        //user get on that bus
                                        //and go to destination
                                        for (Station station : bus_switch2.getStops()) {
                                            if (station.getName().equals(dest.getName())) {
                                                ArrayList<String> full_route_con = new ArrayList<>();
                                                ArrayList<String> full_summary_con = new ArrayList<>();
                                                if (!done_one_con) {
                                                    if (!station1.getName().equals(ori.getName()) && !station1.getName().equals(dest.getName())) {
                                                        if (!station2.getName().equals(ori.getName()) && !station2.getName().equals(dest.getName())) {
                                                            double interval = 0;
                                                            connect_bus_route = new Route();

                                                            Map<String, Map> maps_origin = walkingFromOrigin_BUS(ori);
                                                            Map<String, Map> maps_dest = walkingToDestination_BUS(station);

                                                            Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                                                            Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                                                            Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                                                            ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                                                            ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                                                            Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                                                            ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                                                            ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                                                            DirectionsResult result_o = hm_results_o.get("ResultValue");
                                                            double duration_origin_walking = hm_values_o.get("DurationValue");

                                                            Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                                                            DirectionsResult result_d = hm_results_d.get("ResultValue");
                                                            Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                                                            double duration_dest_walking = hm_values_d.get("DurationValue");

                                                            connect_bus_route.setDestination_walking(result_d);
                                                            connect_bus_route.setOrigin_walking(result_o);
                                                            double o_length = connect_bus_route.origin_walking.routes[0].legs[0].distance.inMeters;
                                                            double d_length = connect_bus_route.destination_walking.routes[0].legs[0].distance.inMeters;
                                                            int total_route_length = (int) (o_length + d_length);
                                                            connect_bus_route.setTotal_route_length(total_route_length);

                                                            for (String ins: instructions_o) {
                                                                full_route_con.add(ins);
                                                            }
                                                            for (String ins: summary_o) {
                                                                full_summary_con.add(ins);
                                                            }
                                                            full_route_con.add("---------CONNECTING--------");
                                                            if (bus.getName() != bus_switch2.getName()) {
                                                                String ori_ins = "Take the Bus " + bus.getName() + " from " + ori.getName();
                                                                full_route_con.add(ori_ins);
                                                                int stops = Math.abs(getPosOrigin_BUS(bus, ori) - getPosOrigin_BUS(bus, station2));
                                                                String ori_ins2 = "Ride for " + stops + " stop(s)";
                                                                full_route_con.add(ori_ins2);
                                                                String switch_ = "Get off at " + station2.getName();
                                                                full_route_con.add(switch_);
                                                                String ins1 = "Change: Then take the Bus " + bus_switch2.getName() + " from " + station1.getName();
                                                                full_route_con.add(ins1);
                                                                int ridestops = Math.abs(getPosOrigin_BUS(bus_switch2, station1) - getPosOrigin_BUS(bus_switch2, station));
                                                                String ins2 = "Ride for " + ridestops + " stop(s)";
                                                                full_route_con.add(ins2);
                                                                String ins3 = "Then get off at " + station.getName();
                                                                full_route_con.add(ins3);
                                                                full_route_con.add("---------CONNECTING--------");
                                                                full_summary_con.add(" 🚶‍ ");
                                                                full_summary_con.add(" ➡ " + bus.getName() + " ➡ ");
                                                                full_summary_con.add(bus_switch2.getName() + " ➡ ");
                                                                full_summary_con.add(" 🚶‍ ");

                                                                //interval between each stop
                                                                double time_1st_con = stops * tinyDB.getDouble(String.valueOf(bus.getName()));
                                                                double time_2nd_con = ridestops * tinyDB.getDouble(String.valueOf(bus_switch2.getName()));

                                                                int total_stops_travel_time = (int) (time_1st_con + time_2nd_con);

                                                                for (String ins: instructions_d) {
                                                                    full_route_con.add(ins);
                                                                }
                                                                for (String ins: summary_d) {
                                                                    full_summary_con.add(ins);
                                                                }
                                                                connect_bus_route.setInstructions(full_route_con);
                                                                connect_bus_route.setStops_duration(total_stops_travel_time);
                                                                connect_bus_route.setTotal_route_duration(total_stops_travel_time);
                                                                connect_bus_route.setSummary(full_summary_con);
                                                                connect_bus_route.setOrigin(ori);
                                                                connect_bus_route.setDestination(station);
                                                                Log.e("connecting done", "done");
                                                                done_one_con = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return done_one_con;
    }

    @SuppressLint({"NewApi", "LongLogTag"})
    private boolean simple_intermodal_bus_lrt(Station origin, Station destination) {
        ArrayList<String> riding = new ArrayList<>();
        ArrayList<Station> riding_stations_list = new ArrayList<>();
        ArrayList<String> ins = new ArrayList<>();
        ArrayList<String> ins_summary = new ArrayList<>();

        boolean yes = false;
        int dest = getPosDest(destination);
        int ori = getPosOrigin(origin);

        int ride = Math.abs(dest - ori);
        if (dest < ori) {
            for (Station station: allLRTStations_reversed) {
                if (station.getName().equals(destination.getName())) {
                    Map<String, Map> maps_origin = walkingFromOrigin_LRT(origin);
                    Map<String, Map> maps_dest = walkingToDestination_LRT(station);

                    Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                    Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                    //ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                    //ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                    Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                    Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                    ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                    ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                    DirectionsResult result_o = hm_results_o.get("ResultValue");
                    DirectionsResult result_d = hm_results_d.get("ResultValue");

                    //walking duration
                    double duration_origin_walking = hm_values_o.get("DurationValue");
                    double duration_dest_walking = hm_values_d.get("DurationValue");

                    intermodal.setOrigin_walking(result_o);
                    intermodal.setDestination_walking(result_d);

                    double o_length = intermodal.origin_walking.routes[0].legs[0].distance.inMeters;
                    double d_length = intermodal.destination_walking.routes[0].legs[0].distance.inMeters;
                    int total_route_length = (int) (o_length + d_length);
                    intermodal.setTotal_route_length(total_route_length);

                    for (Station station1: allLRTStations_reversed) {
                        int pos = getPosDest(station1);
                        if (pos <= ride) {
                            if (!station1.getName().equals(origin.getName())) {
                                riding.add(station1.getName());
                                riding_stations_list.add(station1);
                            }
                        }
                    }
                    //ins.addAll(instructions_o);
                    //ins_summary.addAll(summary_o);
                    ins_summary.add("LRT [RH] ➡ ‍");
                    ins_summary.addAll(summary_d);
                    ins.add("Get the [Towards Rose-Hill] LRV from " + origin.getName());
                    ins.add("Ride for " + ride + " stops");
                    ins.add("Then get off at " + station.getName());
                    ins.addAll(instructions_d);

                    //LRVs arrive every 12min
                    int duration = ((ride * 12));

                    result_o_inter = result_o;
                    result_d_inter = result_d;
                    route_length_inter = total_route_length;
                    full_instructions_inter = ins;
                    full_summary_inter = ins_summary;
                    route_duration_inter = duration;
                    full_station_list_inter = riding_stations_list;

                    intermodal.setRidingStations(riding_stations_list);
                    intermodal.setOrigin(origin);
                    intermodal.setDestination(station);
                    intermodal.setRidingList(riding);
                    intermodal.setInstructions(ins);
                    intermodal.setSummary(ins_summary);
                    intermodal.setTotal_route_duration(duration);
                    return true;
                }
            }
        }
        else {
            for (Station station: allLRTStations) {
                if (station.getName().equals(destination.getName())) {
                    Map<String, Map> maps_origin = walkingFromOrigin_LRT(origin);
                    Map<String, Map> maps_dest = walkingToDestination_LRT(station);

                    Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                    Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                    //ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                    ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                    Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                    Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                    ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                    ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                    DirectionsResult result_o = hm_results_o.get("ResultValue");
                    DirectionsResult result_d = hm_results_d.get("ResultValue");
                    double duration_origin_walking = hm_values_o.get("DurationValue");
                    double duration_dest_walking = hm_values_d.get("DurationValue");

                    intermodal.setOrigin_walking(result_o);
                    intermodal.setDestination_walking(result_d);

                    double o_length = intermodal.origin_walking.routes[0].legs[0].distance.inMeters;
                    double d_length = intermodal.destination_walking.routes[0].legs[0].distance.inMeters;
                    int total_route_length = (int) (o_length + d_length);
                    intermodal.setTotal_route_length(total_route_length);

                    for (Station station1: allLRTStations) {
                        int pos = getPosDest(station1);
                        if (pos <= ride) {
                            if (!station1.getName().equals(origin.getName())) {
                                riding.add(station1.getName());
                                riding_stations_list.add(station1);
                            }
                        }
                    }
                    //ins.addAll(instructions_o);
                    //ins_summary.addAll(summary_o);
                    ins_summary.add("LRT [PL] ➡ ‍");
                    ins_summary.addAll(summary_d);
                    ins.add("Get the [Towards Port Louis] LRV from " + origin.getName());
                    ins.add("Ride for " + ride + " stops");
                    ins.add("Then get off at " + station.getName());
                    ins.addAll(instructions_d);

                    int duration = (int) ((ride * 12));

                    result_o_inter = result_o;
                    result_d_inter = result_d;
                    route_length_inter = total_route_length;
                    full_instructions_inter = ins;
                    full_summary_inter = ins_summary;
                    route_duration_inter = duration;
                    full_station_list_inter = riding_stations_list;

                    intermodal.setRidingStations(riding_stations_list);
                    intermodal.setOrigin(origin);
                    intermodal.setDestination(station);
                    intermodal.setRidingList(riding);
                    intermodal.setInstructions(ins);
                    intermodal.setSummary(ins_summary);
                    intermodal.setTotal_route_duration(duration);
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressLint({"LongLogTag", "NewApi"})
    private boolean direct_LRT (Station origin, Station destination) {
        //hash map for riding stops
        Map<String, Map> main = new HashMap<String, Map>();
        Map<String, ArrayList<Station>> riding_stations_map = new HashMap<String, ArrayList<Station>>();

        ArrayList<String> full_instructions = new ArrayList<>();
        ArrayList<String> full_summary = new ArrayList<>();
        ArrayList<String> riding = new ArrayList<>();
        ArrayList<Station> riding_stations_list = new ArrayList<>();
        direct_lrt_route = new Route();

        boolean yes = false;
        int dest = getPosDest(destination);
        int ori = getPosOrigin(origin);

        int ride = Math.abs(dest - ori);
        if (dest < ori) {
            for (Station station: allLRTStations_reversed) {
                if (station.getName().equals(destination.getName())) {
                    Log.e("Get the [Towards Rose-Hill] LRV from ", origin.getName());
                    Log.e("Ride for", ride + " stops");
                    Log.e("Then get off at", station.getName());
                    for (Station station1: allLRTStations_reversed) {
                        int pos = getPosDest(station1);
                        if (pos <= ride) {
                            if (!station1.getName().equals(origin.getName())) {
                                riding.add(station1.getName());
                                riding_stations_list.add(station1);
                            }
                        }
                    }

                    Map<String, Map> maps_origin = walkingFromOrigin_LRT(origin);
                    Map<String, Map> maps_dest = walkingToDestination_LRT(station);

                    Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                    Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                    ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                    ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                    Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                    Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                    ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                    ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                    DirectionsResult result_o = hm_results_o.get("ResultValue");
                    DirectionsResult result_d = hm_results_d.get("ResultValue");
                    double duration_origin_walking = hm_values_o.get("DurationValue");
                    double duration_dest_walking = hm_values_d.get("DurationValue");

                    full_instructions.addAll(instructions_o);
                    full_instructions.add("----------- MY DIRECTIONS -----------");
                    full_instructions.add("Get the [Towards Rose-Hill] LRV from " + origin.getName());
                    full_instructions.add("Ride for " + ride + " stops");
                    full_instructions.add("Then get off at " + station.getName());
                    full_instructions.add("------------ MY DIRECTIONS -----------");
                    Log.e("riding", ride + "");
                    int duration = ride * 12;
                    Log.e("riding", duration + "");
                    full_summary.addAll(summary_o);
                    String ins_sum = "🚶 ➡ LRT [RH] ➡ 🚶";
                    full_summary.add(ins_sum);
                    full_instructions.addAll(instructions_d);
                    full_summary.addAll(summary_d);


                    riding_stations_map.put("Riding", riding_stations_list);
                    direct_lrt_route.setOrigin_walking(result_o);
                    direct_lrt_route.setDestination_walking(result_d);
                    direct_lrt_route.setOrigin(origin);
                    direct_lrt_route.setDestination(station);
                    direct_lrt_route.setRidingList(riding);
                    direct_lrt_route.setRidingStations(riding_stations_list);
                    direct_lrt_route.setInstructions(full_instructions);
                    direct_lrt_route.setSummary(full_summary);
                    direct_lrt_route.setTotal_route_duration(duration);

                    double o_length = direct_lrt_route.origin_walking.routes[0].legs[0].distance.inMeters;
                    double d_length = direct_lrt_route.destination_walking.routes[0].legs[0].distance.inMeters;
                    int total_route_length = (int) (o_length + d_length);
                    direct_lrt_route.setTotal_route_length(total_route_length);
                    return yes = true;
                }
            }
        }
        else {
            for (Station station: allLRTStations) {
                if (station.getName().equals(destination.getName())) {
                    Log.e("Get the [Towards Port Louis] LRV from ", origin.getName());
                    Log.e("Ride for", ride + " stops");
                    Log.e("Then get off at", station.getName());
                    for (Station station1: allLRTStations) {
                        int pos = getPosDest(station1);
                        if (pos <= ride) {
                            if (!station1.getName().equals(origin.getName())) {
                                riding.add(station1.getName());
                            }
                            riding_stations_list.add(station1);
                        }
                    }

                    Map<String, Map> maps_origin = walkingFromOrigin_LRT(origin);
                    Map<String, Map> maps_dest = walkingToDestination_LRT(station);

                    Map<String, ArrayList> hm_arrays_o = (Map<String, ArrayList>) maps_origin.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_o = (Map<String, DirectionsResult>) maps_origin.get("HashMapResults");
                    Map<String, Double> hm_values_o = (Map<String, Double>) maps_origin.get("HashMapValues");
                    ArrayList<String> instructions_o = (ArrayList<String>) hm_arrays_o.get("InstructionsList");
                    ArrayList<String> summary_o = (ArrayList<String>) hm_arrays_o.get("SummaryList");

                    Map<String, ArrayList> hm_arrays_d = (Map<String, ArrayList>) maps_dest.get("HashMapIns");
                    Map<String, DirectionsResult> hm_results_d = (Map<String, DirectionsResult>) maps_dest.get("HashMapResults");
                    Map<String, Double> hm_values_d = (Map<String, Double>) maps_dest.get("HashMapValues");
                    ArrayList<String> instructions_d = (ArrayList<String>) hm_arrays_d.get("InstructionsList");
                    ArrayList<String> summary_d = (ArrayList<String>) hm_arrays_d.get("SummaryList");

                    DirectionsResult result_o = hm_results_o.get("ResultValue");
                    DirectionsResult result_d = hm_results_d.get("ResultValue");
                    double duration_origin_walking = hm_values_o.get("DurationValue");
                    double duration_dest_walking = hm_values_d.get("DurationValue");

                    full_instructions.addAll(instructions_o);
                    full_instructions.add("----------------- MY DIRECTIONS -----------------");
                    full_instructions.add("Get the [Towards Port Louis] LRV from " + origin.getName());
                    full_instructions.add("Ride for " + ride + " stops");
                    full_instructions.add("Then get off at " + station.getName());
                    full_instructions.add("----------------- MY DIRECTIONS -----------------");

                    full_instructions.addAll(instructions_d);
                    Log.e("riding", ride + "");
                    int duration = ride * 12;
                    Log.e("riding", duration + "");
                    full_summary.addAll(summary_o);
                    full_summary.add("🚶 ➡ LRT [PL] ➡ 🚶");
                    full_summary.addAll(summary_d);

                    direct_lrt_route.setDestination_walking(result_d);
                    direct_lrt_route.setOrigin_walking(result_o);
                    direct_lrt_route.setOrigin(origin);
                    direct_lrt_route.setDestination(station);
                    direct_lrt_route.setRidingList(riding);
                    direct_lrt_route.setInstructions(full_instructions);
                    riding_stations_map.put("Riding", riding_stations_list);
                    direct_lrt_route.setRidingStations(riding_stations_list);
                    direct_lrt_route.setSummary(full_summary);
                    direct_lrt_route.setStops_duration(duration);
                    direct_lrt_route.setTotal_route_duration(duration);
                    return yes = true;
                }
            }
        }
        return yes;
    }

    private int getPosOrigin (Station origin) {
        ArrayList<Station> all_stations = Maps_Full_Access.allLRTStations;
        int ori_pos_in_array = 0;
        for (Station station: all_stations) {
            if (station.getName().equals(origin.getName())) {
                ori_pos_in_array += all_stations.indexOf(station);
            }
        }
        return ori_pos_in_array;
    }

    private int getPosDest (Station destination) {
        ArrayList<Station> all_stations = Maps_Full_Access.allLRTStations;
        int dest_pos_in_array = 0;
        for (Station station: all_stations) {
            if (station.getName().equals(destination.getName())) {
                dest_pos_in_array += all_stations.indexOf(station);
            }
        }
        return dest_pos_in_array;
    }

    private int getPosOrigin_BUS (Bus bus, Station origin) {
        ArrayList<Station> stops = bus.getStops();
        int origin_pos_in_array = 0;
        for (Station station: stops) {
            if (station.getName().equals(origin.getName())) {
                origin_pos_in_array += stops.indexOf(station);
                Log.e("stat " + station.getName(), "pos in " + bus.getName() + " " + origin_pos_in_array);
            }
        }
        return Math.abs(origin_pos_in_array);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Map<String, Map> walkingFromOrigin_BUS(Station orig) {
        Map<String, Map> main = new HashMap<String, Map>();
        Map<String, ArrayList<String>> arrays = new HashMap();
        Map<String, Double> values = new HashMap();
        Map<String, DirectionsResult> result = new HashMap();
        ArrayList<String> instructs_full = new ArrayList<>();
        ArrayList<String> instructs_summary = new ArrayList<>();
        double duration = 0;
        //at this stage direct the user from the station_bus to
        //get them the bus to reach their destination
        String oriStationLatLng = orig.getPosition().latitude + "," + orig.getPosition().longitude;
        DirectionsResult results = getDirectionsDetails(origin.getOrigin(), oriStationLatLng, TravelMode.WALKING);
        if (results != null) {
            int num = results.routes[0].legs[0].steps.length;
            instructs_summary.add(results.routes[0].legs[0].duration.humanReadable);
            duration += Double.parseDouble((results.routes[0].legs[0].duration.humanReadable).replaceAll("[\\D]", ""));
            try {
                for (int i = 0; i < num; i++) {
                    String get = results.routes[0].legs[0].steps[i].htmlInstructions;
                    String aget = results.routes[0].legs[0].steps[i].maneuver;
                    if (aget != null) {
                        Marker maneuver = gMap.addMarker(new MarkerOptions()
                                .title(aget)
                                .position(new LatLng(results.routes[0].legs[0].steps[i].endLocation.lat, results.routes[0].legs[0].steps[i].endLocation.lng))
                                .visible(false)
                                .snippet(aget));
                        maneuver.setTag("M");
                    }
                    String instruction = String.valueOf(Html.fromHtml(get, Html.FROM_HTML_MODE_COMPACT));
                    instructs_full.add(instruction);
                }
                result.put("ResultValue", results);
                arrays.put("InstructionsList", instructs_full);
                arrays.put("SummaryList", instructs_summary);
                values.put("DurationValue", duration);
                values.put("LengthValue", (double) results.routes[0].legs[0].distance.inMeters);

                main.put("HashMapIns", arrays);
                main.put("HashMapValues", values);
                main.put("HashMapResults", result);
            }
            catch (Exception e) {
                Toast.makeText(getContext(), "Failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            rlDirections.setVisibility(View.INVISIBLE);
        }
        return main;
    }

    @SuppressLint("NewApi")
    private Map<String, Map> walkingToDestination_BUS (Station desti) {
        boolean success = false;
        Map<String, Map> main = new HashMap<String, Map>();
        Map<String, ArrayList<String>> arrays = new HashMap();
        Map<String, Double> values = new HashMap();
        Map<String, DirectionsResult> result = new HashMap();
        ArrayList<String> instructs_full = new ArrayList<>();
        ArrayList<String> instructs_summary = new ArrayList<>();
        double duration = 0;
        //at this stage direct the user from the station_bus to
        //get them the bus to reach their destination
        String destiStationLatLng = desti.getPosition().latitude + "," + desti.getPosition().longitude;
        DirectionsResult results = getDirectionsDetails(destiStationLatLng, destination.getDestination(), TravelMode.WALKING);
        if (results != null) {
            int num = results.routes[0].legs[0].steps.length;
            instructs_summary.add(results.routes[0].legs[0].duration.humanReadable);
            duration += Double.parseDouble((results.routes[0].legs[0].duration.humanReadable).replaceAll("[\\D]", ""));
            try {
                for (int i = 0; i < num; i++) {
                    String get = results.routes[0].legs[0].steps[i].htmlInstructions;
                    String aget = results.routes[0].legs[0].steps[i].maneuver;
                    if (aget != null) {
                        Marker maneuver = gMap.addMarker(new MarkerOptions()
                                .title(aget)
                                .position(new LatLng(results.routes[0].legs[0].steps[i].endLocation.lat, results.routes[0].legs[0].steps[i].endLocation.lng))
                                .visible(false)
                                .snippet(aget));
                        maneuver.setTag("M");
                    }
                    String instruction = String.valueOf(Html.fromHtml(get, Html.FROM_HTML_MODE_COMPACT));
                    instructs_full.add(instruction);
                    success = true;
                }
                result.put("ResultValue", results);
                arrays.put("InstructionsList", instructs_full);
                arrays.put("SummaryList", instructs_summary);
                values.put("DurationValue", duration);
                values.put("LengthValue", (double) results.routes[0].legs[0].distance.inMeters);

                main.put("HashMapIns", arrays);
                main.put("HashMapValues", values);
                main.put("HashMapResults", result);
            }
            catch (Exception e) {
                Log.e("er", e.getMessage());
                Toast.makeText(getContext(), e.getMessage() + " herrr Sorry. Something Went Wrong...", Toast.LENGTH_LONG).show();
            }
        }
        return main;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Map<String, Map> walkingFromOrigin_LRT(Station orig) {
        Map<String, Map> main = new HashMap<String, Map>();
        Map<String, ArrayList<String>> arrays = new HashMap();
        Map<String, Double> values = new HashMap();
        Map<String, DirectionsResult> result = new HashMap();
        ArrayList<String> instructs_full = new ArrayList<>();
        ArrayList<String> instructs_summary = new ArrayList<>();
        double duration = 0;
        //at this stage direct the user from the station_bus to
        //get them the bus to reach their destination
        String oriStationLatLng = orig.getPosition().latitude + "," + orig.getPosition().longitude;
        DirectionsResult results = getDirectionsDetails(origin.getOrigin(), oriStationLatLng, TravelMode.WALKING);
        if (results != null) {
            int num = results.routes[0].legs[0].steps.length;
            instructs_summary.add(results.routes[0].legs[0].duration.humanReadable);
            duration += Double.parseDouble((results.routes[0].legs[0].duration.humanReadable).replaceAll("[\\D]", ""));
            try {
                for (int i = 0; i < num; i++) {
                    String get = results.routes[0].legs[0].steps[i].htmlInstructions;
                    String aget = results.routes[0].legs[0].steps[i].maneuver;
                    if (aget != null) {
                        Marker maneuver = gMap.addMarker(new MarkerOptions()
                                .title(aget)
                                .position(new LatLng(results.routes[0].legs[0].steps[i].endLocation.lat, results.routes[0].legs[0].steps[i].endLocation.lng))
                                .visible(false)
                                .snippet(aget));
                        maneuver.setTag("M");
                    }
                    String instruction = String.valueOf(Html.fromHtml(get, Html.FROM_HTML_MODE_COMPACT));
                    instructs_full.add(instruction);
                }
                result.put("ResultValue", results);
                arrays.put("InstructionsList", instructs_full);
                arrays.put("SummaryList", instructs_summary);
                values.put("DurationValue", duration);
                values.put("LengthValue", (double) results.routes[0].legs[0].distance.inMeters);

                main.put("HashMapIns", arrays);
                main.put("HashMapValues", values);
                main.put("HashMapResults", result);
            }
            catch (Exception e) {
                Toast.makeText(getContext(), "Failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            rlDirections.setVisibility(View.INVISIBLE);
        }
        return main;
    }

    @SuppressLint("NewApi")
    private Map<String, Map> walkingToDestination_LRT (Station desti) {
        boolean success = false;
        Map<String, Map> main = new HashMap<String, Map>();
        Map<String, ArrayList<String>> arrays = new HashMap();
        Map<String, Double> values = new HashMap();
        Map<String, DirectionsResult> result = new HashMap();
        ArrayList<String> instructs_full = new ArrayList<>();
        ArrayList<String> instructs_summary = new ArrayList<>();
        double duration = 0;
        //at this stage direct the user from the station_bus to
        //get them the bus to reach their destination
        String destiStationLatLng = desti.getPosition().latitude + "," + desti.getPosition().longitude;
        DirectionsResult results = getDirectionsDetails(destiStationLatLng, destination.getDestination(), TravelMode.WALKING);
        if (results != null) {
            int num = results.routes[0].legs[0].steps.length;
            instructs_summary.add(results.routes[0].legs[0].duration.humanReadable);
            duration += Double.parseDouble((results.routes[0].legs[0].duration.humanReadable).replaceAll("[\\D]", ""));
            try {
                for (int i = 0; i < num; i++) {
                    String get = results.routes[0].legs[0].steps[i].htmlInstructions;
                    String aget = results.routes[0].legs[0].steps[i].maneuver;
                    if (aget != null) {
                        Marker maneuver = gMap.addMarker(new MarkerOptions()
                                .title(aget)
                                .position(new LatLng(results.routes[0].legs[0].steps[i].endLocation.lat, results.routes[0].legs[0].steps[i].endLocation.lng))
                                .visible(false)
                                .snippet(aget));
                        maneuver.setTag("M");
                    }
                    String instruction = String.valueOf(Html.fromHtml(get, Html.FROM_HTML_MODE_COMPACT));
                    instructs_full.add(instruction);
                    success = true;
                }
                result.put("ResultValue", results);
                arrays.put("InstructionsList", instructs_full);
                arrays.put("SummaryList", instructs_summary);
                values.put("DurationValue", duration);
                values.put("LengthValue", (double) results.routes[0].legs[0].distance.inMeters);

                main.put("HashMapIns", arrays);
                main.put("HashMapValues", values);
                main.put("HashMapResults", result);
            }
            catch (Exception e) {
                Log.e("er", e.getMessage());
                Toast.makeText(getContext(), e.getMessage() + " herrr Sorry. Something Went Wrong...", Toast.LENGTH_LONG).show();
            }
        }
        return main;
    }

    private void RouteInstructs() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                computed_layout.setVisibility(View.VISIBLE);
                ArrayList<Route> alt_routes = new ArrayList<>();
                if (direct_bus_route != null)
                    if (direct_bus_route.getInstructions() != null)
                        alt_routes.add(direct_bus_route);
                if (connect_bus_route != null) {
                    alt_routes.add(connect_bus_route);
                }
                if (intermodal_alt != null) {
                    alt_routes.add(intermodal_alt);
                }
                if (intermodal != null) {
                    alt_routes.add(intermodal);
                }

                ArrayList<Route> best_routes = new ArrayList<>();
                if (direct_lrt_route != null) {
                    best_routes.add(direct_lrt_route);
                }
                setAdapter_Alts(alt_routes);
                setAdapter_Best(best_routes);
            }
        }, 5000);
    }

    static void setPrevious (Route route) {
        if (!NavigationActivity.tinyDB.getAll().containsKey("Previous")) {
            previous = route;
            routes_objects.add(previous);
            NavigationActivity.tinyDB.putListObject("Previous", routes_objects);
        }
        else if (!routes_objects.contains(route)) {
            previous = route;
            routes_objects = NavigationActivity.tinyDB.getListObject("Previous", Route.class);
            routes_objects.add(0, previous);
            NavigationActivity.tinyDB.putListObject("Previous", routes_objects);
        }
    }

    private void setAdapter_Alts (ArrayList<Route> routes) {
        Computed_RecyclerAdapter adapter = new Computed_RecyclerAdapter(routes, this.getContext(), this.getView());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView_alts.setLayoutManager(manager);
        recyclerView_alts.setItemAnimator(new DefaultItemAnimator());
        recyclerView_alts.setAdapter(adapter);
    }

    private void setAdapter_Best (ArrayList<Route> routes) {
        Computed_RecyclerAdapter adapter = new Computed_RecyclerAdapter(routes, this.getContext(), this.getView());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView_lrt.setLayoutManager(manager);
        recyclerView_lrt.setItemAnimator(new DefaultItemAnimator());
        recyclerView_lrt.setAdapter(adapter);
    }

    //show the full directions for route
    static void setRouteAdapter (Route route, Context context) {
        computed_layout.setVisibility(View.INVISIBLE);
        gMap.clear();
        journey = new Journey();
        List<LatLng> decodedPath_origin_walk = PolyUtil.decode(route.origin_walking.routes[0].overviewPolyline.getEncodedPath());
        List<LatLng> decodedPath_dest_walk = PolyUtil.decode(route.destination_walking.routes[0].overviewPolyline.getEncodedPath());
        Polyline walk_ori = gMap.addPolyline(new PolylineOptions().addAll(decodedPath_origin_walk));
        Polyline mine = null;
        if (route.getSummary().toString().contains("LRT")) {
            String ori = route.origin.position.latitude + "," + route.origin.position.longitude;
            String dest = route.destination.position.latitude + "," + route.destination.position.longitude;
            DirectionsResult result = getDirectionsDetails(ori, dest, TravelMode.TRANSIT);
            if (result != null) {
                List<LatLng> decodedPath_my_ins = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
                 mine = gMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .addAll(decodedPath_my_ins)
                        .color(GREEN));
            }

            Marker ori_marker = gMap.addMarker(new MarkerOptions()
                    .snippet("Take The LRV.")
                    .title("At This Stage:")
                    .position(route.origin.position));
            Marker dest_marker = gMap.addMarker(new MarkerOptions()
                    .snippet("Get Off The LRV & Walk To Destination.")
                    .title("At This Stage:")
                    .position(route.destination.position));
            dest_marker.setTag("D");
            ori_marker.setTag("O");
        }
        Polyline walk_dest = gMap.addPolyline(new PolylineOptions().addAll(decodedPath_dest_walk));
        walk_ori.setColor(Color.BLUE);
        walk_dest.setColor(Color.RED);
        walk_dest.setWidth(20f);
        walk_ori.setWidth(20f);
        walk_dest.setClickable(true);
        walk_ori.setClickable(true);
        Marker o_walking_start = gMap.addMarker(new MarkerOptions()
                .snippet("Walk To The Station.")
                .title("At This Stage:")
                .position(new LatLng(route.origin_walking.routes[0].legs[0].startLocation.lat, route.origin_walking.routes[0].legs[0].startLocation.lng)));
        Marker d_walking_end = gMap.addMarker(new MarkerOptions()
                .snippet("You Have Reached Your Destination.")
                .title("At This Stage:")
                .position(new LatLng(route.destination_walking.routes[0].legs[0].endLocation.lat, route.destination_walking.routes[0].legs[0].endLocation.lng)));
        d_walking_end.setTag("D");
        o_walking_start.setTag("O");
        gMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(route.origin.position, 13.0f));
        origin_marker = o_walking_start;
        destination_marker = d_walking_end;
        bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_EXPANDED);
        Route_RecyclerAdapter adapter = new Route_RecyclerAdapter(route.getInstructions(), route, context);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView_full.setLayoutManager(manager);
        recyclerView_full.setItemAnimator(new DefaultItemAnimator());
        recyclerView_full.setAdapter(adapter);

        Distance walk_o = route.origin_walking.routes[0].legs[0].distance;
        Distance walk_d = route.destination_walking.routes[0].legs[0].distance;

        journey.setOrigin_walk(route.origin_walking);
        journey.setDestination_walk(route.destination_walking);
        journey.setDest_walk_poly(walk_dest);
        journey.setOri_walk_poly(walk_ori);
        journey.setMy_poly(mine);
    }

    public PolylineOptions draw_bus_polyline (ArrayList<Station> stops) {
        PolylineOptions options = new PolylineOptions();
        Station previous;

        for (int i = 0; i < stops.size(); i++) {
            previous = stops.get(i);
            int next = i + 1;

            if (next < stops.size()) {
                Station station = stops.get(next);
                String origin = String.valueOf(previous.position.latitude) + "," + String.valueOf(previous.position.longitude);
                String destination = String.valueOf(station.position.latitude) + "," + String.valueOf(station.position.longitude);

                //manipulate directionsAPI
                DirectionsResult results = getDirectionsDetails(origin, destination, TravelMode.DRIVING);
                if (results != null) {
                    List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
                    return options.addAll(decodedPath).color(BLUE);
                }
            }
        }
        return options;
    }

    static void show_ride_stops_buses (Context context, ArrayList<String> instructions, String main, Station origin) {
        ride_stops_dialog = new Dialog(context);
        ride_stops_dialog.setContentView(R.layout.dialog_ride_stops);

        TableLayout buses = ride_stops_dialog.findViewById(R.id.riding_stops);
        TextView ride_for = ride_stops_dialog.findViewById(R.id.ride_for);
        TextView ride_from = ride_stops_dialog.findViewById(R.id.ride_from);
        follow_anim = ride_stops_dialog.findViewById(R.id.follow_anim);
        ride_stops_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        ride_for.setText(main);
        ride_from.setText("From: " + origin.getName());
        buses.removeAllViews();
        animateView_follow(follow_anim, true, context);

        for (String ins: instructions) {
            TextView tv = new TextView(ride_stops_dialog.getContext());
            TableRow mainRow = new TableRow(context);
            tv.setText(ins);
            tv.setTextAppearance(context, R.style.ride_stop_style);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.inflate_ride_stop, mainRow);
            mainRow.addView(tv);
            //mainRow.addView(stopDistanceText);

            //onclick listener for each row
            mainRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "clicked " + ins, Toast.LENGTH_LONG).show();
                }
            });
            buses.addView(mainRow);
        }

        ImageButton closeDialog = ride_stops_dialog.findViewById(R.id.dialog_close_ride);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView_follow(follow_anim, false, context);
                ride_stops_dialog.dismiss();
            }
        });
        ride_stops_dialog.show();
    }

    @SuppressLint("NewApi")
    static void animateView_follow (ImageView imageView, boolean animate, Context context) {
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) context.getResources().getDrawable(R.drawable.follow_route);
        // Insert your AnimatedVectorDrawable resource identifier
        imageView.setImageDrawable(d);

        if (animate) {
            d.start();
        }
        else
            d.stop();
    }

    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        com.example.fypmetroapp.LatLng now = new com.example.fypmetroapp.LatLng(location.getLatitude(), location.getLongitude());
        if (role != null) {
            if (role.equals("User")) {
                if (gMap != null) {
                    uid = firebaseAuth.getCurrentUser().getUid();
                    userUpdates.setLocation(location);
                    userUpdates.setLatLngLocation(latLng);
                    userGeoFire.setLocation(uid, new GeoLocation(
                            location.getLatitude(),
                            location.getLongitude()), (key, error) -> {
                        if (userMarker != null) userMarker.remove();

                        userMarker = gMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .visible(false)
                                .title(uid));

                        userUpdates.setLocation(location);
                        userUpdates.setLatLngLocation(latLng);
                        curLocation = location;
                        gMap.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(userMarker.getPosition(), 13.0f));
                    });
                }
            } else if (role.equals("Driver")) {
                if (gMap != null) {
                    Log.e("driver location", "changed");
                }
            }
        }
        tinyDB.putObject("location", now);
    }

    @SuppressLint("MissingPermission")
    private void initStationsFences_Occupancy () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                geofenceList = new ArrayList<>();
                //area identifiers
                allStations = new ArrayList<>();
                allStations.addAll(Maps_Full_Access.setLrtStations());
                allStations.addAll(Maps_Full_Access.setBusStations());
                for (Station station : allStations) {
                    geofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(station.name)
                            .setCircularRegion(
                                    station.position.latitude,
                                    station.position.longitude,
                                    30
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                }

                geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Geofences added
                                // ...
                            }
                        })
                        .addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add geofences
                                // ...
                                Log.e("failed", "fences");
                            }
                        });
            }
        }, 10000);
    }

    static GeofencingRequest getGeofencingRequest_Riding () {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList_riding);
        return builder.build();
    }

    static PendingIntent getGeofencePendingIntent_Riding (Context context) {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent_riding != null) {
            return geofencePendingIntent_riding;
        }
        Intent intent = new Intent(context, BroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent_riding = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent_riding;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(getContext(), OccupancyReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @SuppressLint("MissingPermission")
    static void initRoute_Fences (ArrayList<Station> stations, Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                geofenceList_riding = new ArrayList<>();
                //area identifiers
                for (Station station : stations) {
                    geofenceList_riding.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(station.name)
                            .setCircularRegion(
                                    station.position.latitude,
                                    station.position.longitude,
                                    30
                            )
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                }

                geofencingClient_riding.addGeofences(getGeofencingRequest_Riding(), getGeofencePendingIntent_Riding(context))
                        .addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Geofences added
                                // ...
                                Log.e("added", "new ones");
                            }
                        })
                        .addOnFailureListener((Activity) context, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add geofences
                                // ...
                                Log.e("failed", "fences");
                            }
                        });
            }
        }, 10000);
    }

    @Override
    public void onResume() {
        super.onResume();
        initStationsFences_Occupancy();
    }

    static void sendNotification(String title, String message, Context context) {
        try {
            String NOTIFICATION_CHANNEL_ID = App.NOTIFICATION_SERVICE;
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        "Notifs",
                        NotificationManager.IMPORTANCE_HIGH);

                channel.setDescription("Desccription");
                channel.setLightColor(Color.BLUE);
                channel.enableLights(true);
                channel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.track_location)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(Notification.PRIORITY_HIGH);
            Notification notification = mBuilder.build();
            manager.notify(0, notification);
        }
        catch (Exception e) {
            Log.e("failed", e.getMessage());
        }
    }
}