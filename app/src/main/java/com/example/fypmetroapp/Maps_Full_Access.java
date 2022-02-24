package com.example.fypmetroapp;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.tomtom.online.sdk.routing.RoutingApi;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.green;

public class Maps_Full_Access extends Fragment implements LocationListener {

    //Variable Declarations
    public static GoogleMap gMap;
    public static GoogleMap map_displaying_extras;
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker lrtMarker;
    private Marker busMarker;
    Marker bus_location;
    ValueEventListener busListener;
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
    static LinearLayout llNearbySheet, llBus_Stations_Route, llFollow_Route, llBottomSheet, llLRT_StationSheet_Sche, llBUS_StationSheet_Sche;
    private Polyline polyline_LRT;
    private Polyline polyline_BUS;
    private Polyline polyline_Directions;
    static BottomSheetBehavior<LinearLayout> bottomSheetBehavior_NearBy,
            bottomSheetBehavior_Buses_Stations_Route,
            bottomSheetBehavior_Directions, bottomSheetBehavior_LRT_ClickedStation_Sche;
    FloatingActionButton fabDirections;
    LocationCallback locationCallback;
    static ArrayList<Marker> allLRTMarkers, allBusMarkers, allMarkers;
    static ArrayList<Station> allBusStations, allLRTStations, allLRTStations_reversed, allStations;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    DatabaseReference userRef;
    DatabaseReference driverRef;
    TextView timeText;
    TextView directionText;
    TextView busText, busAtStation;
    TextView clickedBus;
    ImageButton directionsButton, fav_to, fav_from, close_Dir, selectLoc, selectReverse;
    public static final int overview = 0;
    static CardView nearbyCardView, lrt_mode, bus_mode, chooseTime, close_routes;
    static ArrayList<Bus> allBuses;
    int distance = 0;
    GeoFire userGeoFire;
    GeoFire driverGeoFire;
    public static ArrayList<Polyline> bus_route = new ArrayList<>();
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
    static List<StationFence> stationsFences;
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

    LinearLayout following_route;
    final static ArrayList<Route> routes = new ArrayList<>();
    static ArrayList<Object> routes_objects = new ArrayList<>();
    static FragmentManager fragmentManager;
    boolean done_or = false;
    static Marker origin_marker, ori_next_marker, dest_next_marker;

    //Class Declarations
    Origin origin = new Origin();
    Destination destination = new Destination();
    UserUpdates userUpdates = new UserUpdates();
    SearchPlace searchPlace = new SearchPlace();
    Route direct_bus_route = null;
    Route connect_bus_route = null;
    Route direct_lrt_route = null;
    Route intermodal = null;
    Route intermodal_alt = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.maps_full_access, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = this.getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        tinyDB = new TinyDB(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        stationDetailsDialog = new Dialog(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        following_route = getView().findViewById(R.id.following_route);
        progressDialog = new ProgressDialog(getContext());
        favourites = new ArrayList<Map<String, String>>();
        foundfences = new ArrayList<>();
        fabDirections = getView().findViewById(R.id.fab_directions_maps);

        timeText = null;
        directionText = null;
        nearbyCardView = getView().findViewById(R.id.nearbystations);
        directionsButton = getView().findViewById(R.id.startDirections);

        stationDetailsDialog = new Dialog(getContext());
        materialSearchBar = getView().findViewById(R.id.searchBar);
        Places.initialize(this.getContext(), Config.MYAPI_KEY);
        placesClient = Places.createClient(getContext());

        clickedBus = getView().findViewById(R.id.clickedBus);
        llBottomSheet = getView().findViewById(R.id.bottom_sheet);
        llNearbySheet = getView().findViewById(R.id.bottom_nearby_sheet);
        llLRT_StationSheet_Sche = getView().findViewById(R.id.bottom_station_sheet_sche);

        textAddress = llBottomSheet.findViewById(R.id.textAddress);
        titleText = llBottomSheet.findViewById(R.id.titleText);
        txtClicked_LRT_Station = llLRT_StationSheet_Sche.findViewById(R.id.txtSelectedtation);

        AutocompleteSessionToken sessionToken = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled == true) {
                    searchButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == materialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                    materialSearchBar.setVisibility(View.INVISIBLE);
                    searchButton.setVisibility(View.VISIBLE);
                }
            }
        });
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountry("MU")
                        .setSessionToken(sessionToken)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse response = task.getResult();
                            if (response != null) {
                                predlist = response.getAutocompletePredictions();
                                List<String> predictions = new ArrayList<>();

                                for (AutocompletePrediction prediction: predlist) {
                                    predictions.add(prediction.getFullText(null).toString());
                                }

                                materialSearchBar.updateLastSuggestions(predictions);
                                if (materialSearchBar.isSuggestionsVisible())
                                    materialSearchBar.showSuggestionsList();
                            }
                        }
                        else {

                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predlist.size()) {
                    return;
                }
                AutocompletePrediction choice = predlist.get(position);
                String aChoice = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(aChoice);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1500);

                InputMethodManager methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (methodManager != null)
                    methodManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                String choice_id = choice.getPlaceId();
                List<Place.Field> place_details = Arrays.asList(Place.Field.values());

                FetchPlaceRequest request = FetchPlaceRequest.builder(choice_id, place_details).build();
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng position = place.getLatLng();
                        String name = place.getName();
                        String address = place.getAddress();
                        String phone = place.getPhoneNumber();
                        Boolean open = place.isOpen();
                        searchPlace.setPosition(position);
                        searchPlace.setName(name);
                        searchPlace.setAddress(address);
                        searchPlace.setPhone(phone);
                        if (open != null)
                            searchPlace.setOpen(open);
                        else
                            searchPlace.setOpen(false);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));
                        updateMarkerComponent();
                        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        fabDirections.setOnClickListener(Buttons);
        directionsButton.setOnClickListener(Buttons);
        nearbyCardView.setOnClickListener(Buttons);




        role = tinyDB.getString("role");

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.mainMapFrags);
                        mapFragment.getMapAsync(Maps_Full_Access.this::onMapReady);
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



        initMarkerSheet();
        initNearByComponent();
        initLRT_StationScheduleSheet();
        initClicked_BUS_Component();
        HomeFragment_User.update_intervals();
        //supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMapFrags);
        //supportMapFragment.getMapAsync(this::onMapReady);
    }

    //update user's favourite locations
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    };

    SharedPreferences.OnSharedPreferenceChangeListener listener_db = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (role.equals("User")) {
                Log.e("prefs", key);
                HomeFragment_User.update_intervals();
                if (key.equals("Previous")) HomeFragment_User.update_previous();
                HomeFragment_User.update_favourites();
            }
        }
    };

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

        gMap.setOnMarkerClickListener(Maps_Full_Access.this::onMarkerClick);

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
                    //searchButton.callOnClick();
                    if (materialSearchBar.getVisibility() == View.VISIBLE)
                        materialSearchBar.setVisibility(View.INVISIBLE);
                    else
                        materialSearchBar.setVisibility(View.VISIBLE);
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
        if (role != null)
            if (role.equals("User"))
                LRT_Polyline();
        addRailStations();
        addBusStations();

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Marker clicked = gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(latLng.toString()));

                clicked.setTag("P");
            }
        });

        gMap.setOnMapClickListener(latLng -> {
            removeLRTPoly();
            removeBUSPoly();
            removeDirectionsPoly();
            //hide fabD button and sheet
            if (nearbyCardView.getVisibility() == View.INVISIBLE)
                nearbyCardView.setVisibility(View.VISIBLE);

        });
        if (getPolyline_LRT() != null)
            getPolyline_LRT().setVisible(!getPolyline_LRT().isVisible());

        addBuses();

        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterRenderer = new MarkerClusterRenderer<>(getContext(), googleMap, clusterManager);
        setupClusterManager();
        allStations = new ArrayList<>();
        addAllStations();
    }

    private void addClusterItems() {
        for(Marker markerOptions : getAllMarkers()){
            double lat = markerOptions.getPosition().latitude;
            double lon = markerOptions.getPosition().longitude;
            MarkerClusterItem clusterItem = new MarkerClusterItem(new LatLng(lat, lon), markerOptions.getTitle());
            clusterManager.addItem(clusterItem);
        }
    }

    private void setRenderer() {
        MarkerClusterRenderer<MarkerClusterItem> clusterRenderer = new MarkerClusterRenderer<>(getContext(), gMap, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
    }

    private void setupClusterManager() {
//        addClusterItems();
//        setClusterManagerClickListener();
//        setRenderer();
//        clusterManager.cluster();
    }

    private void setClusterManagerClickListener() {
        clusterManager.setOnClusterClickListener(cluster -> {
            Collection<MarkerClusterItem> listItems = cluster.getItems();
            List<String> listNames = new ArrayList<>();
            for (MarkerClusterItem item : listItems){
                listNames.add(item.getTitle());
            }
            gMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    for (MarkerClusterItem item : listItems){
                        Log.e("cluster", item.getTitle());
                    }
                }
                @Override
                public void onCancel() { }
            });
            return true;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static List<StationFence> getStationsFences() {
        return stationsFences;
    }

    public static void setStationsFences(List<StationFence> stationsFences) {
        Maps_Full_Access.stationsFences = stationsFences;
    }

    private void GeoFireConfig() {
        userRef = FirebaseDatabase.getInstance().getReference("User");
        driverRef = FirebaseDatabase.getInstance().getReference("tracking");
        userGeoFire = new GeoFire(userRef);
        driverGeoFire = new GeoFire(driverRef);
    }

    static ArrayList<Marker> getAllMarkers() {
        ArrayList<Marker> markers = new ArrayList<>();
        for (int i = 0; i < allLRTMarkers.size(); i++) {
            markers.add(allLRTMarkers.get(i));
        }
        for (int y = 0; y < allBusMarkers.size(); y++) {
            markers.add(allBusMarkers.get(y));
        }
        return markers;
    }

    static void setAllMarkers(ArrayList<Marker> allMarkers) {
        Maps_Full_Access.allMarkers = allMarkers;
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
        role = tinyDB.getString("role");
        if (role != null) {
            if (role.equals("User")) {
                List<PatternItem> dashedPattern = Arrays.asList(new Dash(60), new Gap(60));
                List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
                polyline_LRT = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                polyline_LRT.setColor(this.getContext().getResources().getColor(R.color.quantum_googred));
                polyline_LRT.setPattern(dashedPattern);
                setPolyline_LRT(polyline_LRT);
            }
        }
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
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

    private DirectionsResult getWalkingDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .optimizeWaypoints(true)
                    .await();
        }
        catch (ApiException | IOException | InterruptedException e) {
            Log.e("error", e.getMessage());
            return null;
        }
    }

    public GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setApiKey(Config.MYAPI_KEY)
                .setConnectTimeout(0, TimeUnit.SECONDS)
                .setReadTimeout(0, TimeUnit.SECONDS)
                .setWriteTimeout(0, TimeUnit.SECONDS);
    }

    //abusing Google's DirectionsAPI for Metro Route
    public void LRT_Polyline() {
        DirectionsResult results = getDirectionsDetails
                ("Rose Hill Central, Beau Bassin-Rose Hill",
                        "Port Louis Victoria, Port Louis",
                        TravelMode.TRANSIT);
        if (results != null) {
            addLRTPolyline(results, gMap);
            positionCamera(results.routes[overview], gMap);
        }
    }

    public double getBus_int() {
        return bus_int;
    }

    public void setBus_int(double bus_int) {
        this.bus_int = bus_int;
    }

    //event listener for buttons
    android.view.View.OnClickListener Buttons = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.fab_directions:
                    NearMeHolder.maps_flipper.showNext();
                    break;
                case R.id.startDirections:
                    NearMeHolder.maps_flipper.showNext();
                    break;
                case R.id.nearbystations:
                    bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    ShowNearestStations();
                    break;
            }
        }
    };

    private void initMarkerSheet () {
        // get the bottom sheet view
        llBottomSheet = getView().findViewById(R.id.bottom_sheet);

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
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fabDirections.hide();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        fabDirections.show();
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

    private void updateMarkerComponent() {
        // change the state of the bottom sheet
        bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_EXPANDED);
        fabDirections.show();
        TextView title, address, phone, distance, open;
        title = llBottomSheet.findViewById(R.id.titleText);
        address = llBottomSheet.findViewById(R.id.textAddress);
        phone = llBottomSheet.findViewById(R.id.txtPhoneNum);
        distance = llBottomSheet.findViewById(R.id.distanceText);
        open = llBottomSheet.findViewById(R.id.open);
        title.setText(searchPlace.getName());
        address.setText(searchPlace.getAddress());
        phone.setText(searchPlace.getPhone());
        if (curLocationLatLng != null) {
            int dist = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, searchPlace.getPosition());
            distance.setText(dist + " metres away");
        }
        else
            Log.e("NO", "LOC SET");
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
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float h = bottomSheet.getHeight();
                float off = h * slideOffset;

                switch (bottomSheetBehavior_NearBy.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        setMapPadding(slideOffset);
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
        // get the bottom sheet view
        llLRT_StationSheet_Sche = getView().findViewById(R.id.bottom_station_sheet_sche);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new ToPortLouisFragment(), "To Port Louis");
        adapter.addFragment(new ToRoseHillFragment(), "To Rose Hill");
        //get the view pager and add fragments to it at runtime
        viewPager = llLRT_StationSheet_Sche.findViewById(R.id.viewpager);
        tabLayout = llLRT_StationSheet_Sche.findViewById(R.id.tabLayout);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


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

        TableLayout mainTable_LRT_stations = getView().findViewById(R.id.mainTable_stations);
        TableLayout mainTable_BUS_stations = getView().findViewById(R.id.mainTable_buses);


        if (mainTable_LRT_stations != null) mainTable_LRT_stations.removeAllViews();
        if (mainTable_BUS_stations != null) mainTable_BUS_stations.removeAllViews();


        //nearest stations
        ArrayList<Station> nearbyStations_lrt = new ArrayList<>();
        ArrayList<Station> nearbyStations_bus = new ArrayList<>();
        int distance = 0;
        int max = Integer.MAX_VALUE;

        for (int i = 0; i < allLRTMarkers.size(); i++) {
            Marker LRTmarker = allLRTMarkers.get(i);
            allMarkers.add(LRTmarker);
            if (userUpdates.latLngLocation != null) {
                if (userMarker != null) {
                    curLocationLatLng = userUpdates.latLngLocation;
                    distance = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, LRTmarker.getPosition());
                }
            }

            Station LRTstation = new Station();
            LRTstation.name = LRTmarker.getTitle();
            LRTstation.distance = distance;
            LRTstation.position = LRTmarker.getPosition();
            LRTstation.type = "LRT";
            nearbyStations_lrt.add(LRTstation);
        }
        for (int y = 0; y < allBusMarkers.size(); y++) {
            Marker BUSmarker = allBusMarkers.get(y);

            allMarkers.add(BUSmarker);
            if (userUpdates.latLngLocation != null) {
                if (userMarker != null) {
                    curLocationLatLng = userUpdates.latLngLocation;
                    distance = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, BUSmarker.getPosition());
                }
            }

            Station BUSstation = new Station();
            BUSstation.name = BUSmarker.getTitle();
            BUSstation.distance = distance;
            BUSstation.position = BUSmarker.getPosition();
            BUSstation.type = "BUS";
            nearbyStations_bus.add(BUSstation);
        }

        setAllMarkers(allMarkers);
        //sort by closest distance
        Collections.sort(nearbyStations_lrt, new SortByDistance());
        Collections.sort(nearbyStations_bus, new SortByDistance());
        int green = getResources().getColor(R.color.quantum_googgreen);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //inflate each LRT station_bus to view
        for (Station station: nearbyStations_lrt) {
            LayoutInflater inflater = Maps_Full_Access.this.getLayoutInflater();
            TableRow mainRow = new TableRow(Maps_Full_Access.this.getContext());
            mainRow.setOrientation(LinearLayout.VERTICAL);

                inflater.inflate(R.layout.lrt_row_to_inflate, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());
                //int distance_to_nearest = (int) SphericalUtil.computeDistanceBetween(curLocation, station_bus.distance);
                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                stopDistanceText.setText("\tless than: " + station.distance + "m away");
                stopDistanceText.setTextAppearance(R.style.ride_stop_style);
                stopDistanceText.setTextColor(green);
                //stopDistanceText.setLayoutParams(params);

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.stat_text_style);
                //stopText.setLayoutParams(params);
                stopText.setPadding(0, 40, 0, 0);

                mainRow.addView(stopText);
                mainRow.addView(stopDistanceText);
                //onclick listener for each row
                mainRow.setOnClickListener(v -> ClickedNearbyStation(station.name));
                if (mainTable_LRT_stations != null) mainTable_LRT_stations.addView(mainRow);
        }
        for (Station station: nearbyStations_bus) {
            LayoutInflater inflater = Maps_Full_Access.this.getLayoutInflater();
            TableRow mainRow = new TableRow(Maps_Full_Access.this.getContext());
            mainRow.setOrientation(LinearLayout.VERTICAL);
                inflater.inflate(R.layout.bus_row_to_inflate, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());

                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                stopDistanceText.setText("\tless than: " + station.distance + "m away");
                stopDistanceText.setTextAppearance(R.style.ride_stop_style);
                stopDistanceText.setTextColor(green);

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.stat_text_style);
                stopText.setPadding(0, 40, 0, 0);

                mainRow.addView(stopText);
                mainRow.addView(stopDistanceText);
                //onclick listener for each row
                mainRow.setOnClickListener(v -> ClickedNearbyStation(station.name));
                if (mainTable_BUS_stations != null) mainTable_BUS_stations.addView(mainRow);
        }
    }

    private boolean onMarkerClick(Marker marker) {
        try {
            if (marker.getTag().equals("P")) {
                titleText.setText(marker.getTitle());
                textAddress.setText(marker.getPosition().toString());
                //show it as collapsed
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_EXPANDED);
                //hide all the other bottom sheets
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
            else if (marker.getTag().equals("LRT")) {
                txtClicked_LRT_Station.setText(marker.getTitle());
                //draw the LRT polyline on click
                polyline_LRT.setVisible(true);
                //show clicked station_bus sheet as half expanded
                bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

                //hide the rest of the bottom sheets
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                fabDirections.hide();
                nearbyCardView.setVisibility(View.INVISIBLE);
                gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                onLRT_StationClick(marker);
            }
            else if (marker.getTag().equals("BUS")) {
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
                /*show clicked station_bus sheet as half expanded
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
            }
            else if (marker.getTag().equals("D")) marker.showInfoWindow();
            else if (marker.getTag().equals("O")) marker.showInfoWindow();
            else if (marker.getTag().equals("M")) marker.showInfoWindow();

        } catch (Exception e) {
        }
        return true;
    }

    public void onBUS_StationClick(Marker marker) {
        try {
            stationDetailsDialog = new Dialog(getContext());
            stationDetailsDialog.setContentView(R.layout.dialog_clicked_station);
            txtClicked_BUS_Station = stationDetailsDialog.findViewById(R.id.txtBusStation_clicked);
            txtClicked_BUS_Station.setText(marker.getTitle());
            String occupancy = occupancy(marker.getTitle());
            stationDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
            ImageButton closeDialog = stationDetailsDialog.findViewById(R.id.dialog_closeX);
            TextView tv = stationDetailsDialog.findViewById(R.id.stat_occupancy);
            tv.setText(occupancy);
            occupancy_anim = stationDetailsDialog.findViewById(R.id.occupancy_anim);
            Extract_BUS_Data(marker);
            TableLayout buses = stationDetailsDialog.findViewById(R.id.buses_at_station_clicked);
            buses.removeAllViews();
            animateView(occupancy_anim, true);
            stationDetailsDialog.show();
            closeDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateView(occupancy_anim, false);
                    stationDetailsDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("failed", e.getMessage());
        }
        //TableLayout buses = getView().findViewById(R.id.buses_at_station);
    }

    static String occupancy (String station_name) {
        for (Station station: allStations) {
            if (station.name.equals(station_name))
                if (station.occupancy != 0)
                    if (station.occupancy >= 30)
                        return "Very Busy";
                    else if (station.occupancy >= 20)
                        return "Relatively Busy";
                    else if (station.occupancy >= 10)
                        return "Normal";
                    else
                        return  "Vacant";
        }
        return "N";
    }

    @SuppressLint("NewApi")
    private void animateView (ImageView imageView, boolean animate) {
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.live_anim);
        // Insert your AnimatedVectorDrawable resource identifier
        occupancy_anim.setImageDrawable(d);

        if (animate) {
            d.start();
        }
        else
            d.stop();
    }

    //show the stations for a bus route
    @SuppressLint("NewApi")
    private void ShowRouteStations(Bus bus) {
        boolean tracking = false;
        bottomSheetBehavior_LRT_ClickedStation_Sche.setState(BottomSheetBehavior.STATE_HIDDEN);
        stationDetailsDialog.dismiss();
        bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        TableLayout mainTable_stations = getView().findViewById(R.id.mainTable_Route);
        mainTable_stations.removeAllViews();
        TextView clicked = llBus_Stations_Route.findViewById(R.id.clickedBus);
        clicked.setText(String.valueOf(bus.getName()));
        //nearest stations
        ArrayList<Station> this_buses_stops = bus.getStops();
        ArrayList<Station> nearbyStations = new ArrayList<>();
        int distance = 0;

        ImageView iv = (ImageView) getView().findViewById(R.id.track_bus);
        ImageView iv_stop = (ImageView) getView().findViewById(R.id.stop_track_bus);


        iv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_stop.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                bus_location.remove();
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().
                        getReference("tracking")
                        .child("BUS")
                        .child(String.valueOf(bus.getName()));
                mDatabase.removeEventListener(busListener);
            }
        });

        iv_stop.setVisibility(View.GONE);
        start_pulse(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_stop.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
                start_pulse(iv_stop);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                        getReference("tracking")
                        .child("BUS")
                        .child(String.valueOf(bus.getName()));
                busListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Bus object and use the values to update the UI
                        //
                        Bus_Tracker bus_tracked = dataSnapshot.getValue(Bus_Tracker.class);
                        Log.e("TRACKING", bus_tracked.name);
                        Log.e("BUS MOVED! NOW AT", bus_tracked.position.toString() + "");

                        //custom marker size
                        int height = 135;
                        int width = 100;
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bus_pin_inflate);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                        if (bus_location != null)
                            bus_location.remove();
                        bus_location = gMap.addMarker(
                                new MarkerOptions()
                                        .icon(smallMarkerIcon)
                                        .position(new LatLng(bus_tracked.position.getLatitude(), bus_tracked.position.getLongitude()))
                                        .snippet(bus_tracked.name)
                        );
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bus_location.getPosition(), 18.5f));
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(18.5f), 2000, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                };
                mDatabase.addValueEventListener(busListener);
            }
        });

        for (int y = 0; y < this_buses_stops.size(); y++) {
            Station station = this_buses_stops.get(y);
            if (userUpdates.location != null) {
                curLocationLatLng = new LatLng(userUpdates.location.getLatitude(), userUpdates.location.getLongitude());
                distance = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, station.position);
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

        //inflate each LRT station_bus to view
        for (int x = 0; x < nearbyStations.size(); x++) {
            Station station = nearbyStations.get(x);

            LayoutInflater inflater = Maps_Full_Access.this.getLayoutInflater();
            TableRow mainRow = new TableRow(Maps_Full_Access.this.getContext());

            if (station.type == Config.STATION_TYPE_BUS) {
                inflater.inflate(R.layout.bus_route_stations, mainRow);

                stopText = new TextView(mainRow.getContext());
                stopDistanceText = new TextView(mainRow.getContext());
                busText = new TextView(mainRow.getContext());

                //user distance from stops CLOSEST-TO-FARTHEST
                //TODO: SHOW DISTANCE IN MINUTES ACCORDING TO CURRENT VEHICLE/TRANSPORT/SPEED
                //stopDistanceText.setText("~" + station_bus.distance + "m away");

                stopText.setText(station.name);
                stopText.setTextAppearance(R.style.stat_text_style);
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

    @SuppressLint("NewApi")
    private void start_pulse (ImageView iv) {
         ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                iv,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(5);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();
    }

    static void hide_all_except (View dont_hide, ViewGroup root) {
        for (int i = 0 ; i < root.getChildCount(); i++){
            View view = root.getChildAt(i);
            if (view.getId() == dont_hide.getId()) {
                dont_hide.setVisibility(View.VISIBLE);
            }
            else {
                view.setVisibility(View.INVISIBLE);
            }
        }
        if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_HIDDEN)
            nearbyCardView.setVisibility(View.VISIBLE);
        if (llNearbySheet.getVisibility() == View.INVISIBLE)
            nearbyCardView.setVisibility(View.VISIBLE);
        if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_EXPANDED)
            nearbyCardView.setVisibility(View.INVISIBLE);
    }

    public void removeLRTPoly() {
        if (getPolyline_LRT() != null) {
            getPolyline_LRT().setVisible(!getPolyline_LRT().isVisible());
        }
    }

    public void removeBUSPoly() {
        //remove whole bus POLYLINE
        if (bus_route != null) {
            for (Polyline line : bus_route) {
                line.remove();
            }
            bus_route.clear();
        }
    }

    public void removeDirectionsPoly() {
        //remove whole bus POLYLINE
        if (bus_route != null) {
            for (Polyline line : bus_route) {
                line.remove();
            }
            bus_route.clear();
        }
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

    //get the station_bus user clicked
    public void ClickedNearbyStation(String name) {
        nearbyCardView.setVisibility(View.INVISIBLE);
        if (GetAllLRTNames().contains(name)) {
            for (int i = 0; i < allLRTMarkers.size(); i++) {
                Marker Trainmarker = allLRTMarkers.get(i);
                //if the station_bus name is the same as the marker call OnMarkerClick
                if (name.equals(Trainmarker.getTitle())) {
                    onMarkerClick(Trainmarker);
                }
            }
        } else if (GetAllBUSNames().contains(name)) {
            for (int i = 0; i < allBusMarkers.size(); i++) {
                Marker Busmarker = allBusMarkers.get(i);
                //if the station_bus name is the same as the marker call OnMarkerClick
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

    //find station_bus times using only STATION NAME returns JSONArray
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
        RequestQueue requestQueue = Volley.newRequestQueue(Maps_Full_Access.this.getContext());
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

            TableLayout table_buses = stationDetailsDialog.findViewById(R.id.buses_at_station_clicked);
            TableLayout table_facilities = stationDetailsDialog.findViewById(R.id.facilities_at_station_clicked);

            View inflated_buses = LayoutInflater.from(getContext()).inflate(R.layout.bus_to_inflate, table_buses, false);
            View inflated_facilities = LayoutInflater.from(getContext()).inflate(R.layout.facility_to_inflate, table_facilities, false);

            table_buses.addView(inflated_buses);
            table_facilities.addView(inflated_facilities);

            FlexboxLayout flexboxBuses = table_buses.findViewById(R.id.flexboxBuses);
            FlexboxLayout flexboxFacilities = table_facilities.findViewById(R.id.flexboxFacilities);
            flexboxBuses.removeAllViews();
            flexboxFacilities.removeAllViews();

            //show buses at station_bus
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
                            if (bus_route != null)
                                removeBUSPoly();
                            ShowRouteStations(thisBus);
                            new Bus().busRoute(thisBus, gMap, bus_route);
                        }
                    }
                });
            }

            //SnackBar tip component
            /*Snackbar.make(table_buses, "Click a bus too see its route!",
                    Snackbar.LENGTH_SHORT)
                    .show();*/

            //show parking availability at station_bus
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

            //show wheelchair access at station_bus
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

            //show bike rack availability at station_bus
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

    //add LRT station_bus markers to map
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

    private void addAllStations () {
        addRailStations();
        addBusStations();
        for (Station station: allBusStations)
            allStations.add(station);
        for (Station station: allLRTStations)
            allStations.add(station);
    }

    //TODO: CHANGE IMPLEMENTATION OF LRT STATIONS
    static ArrayList<Station> setLrtStations () {
        ArrayList<Station> stations = new ArrayList<>();
        allLRTStations_reversed = new ArrayList<>();
        stations.add(new RoseHill().addStation());
        stations.add(new Vander().addStation());
        stations.add(new BeauB().addStation());
        stations.add(new Barkly().addStation());
        stations.add(new Corom().addStation());
        stations.add(new StLouis().addStation());
        stations.add(new PortLouis().addStation());

        allLRTStations_reversed.add(new PortLouis().addStation());
        allLRTStations_reversed.add(new StLouis().addStation());
        allLRTStations_reversed.add(new Corom().addStation());
        allLRTStations_reversed.add(new Barkly().addStation());
        allLRTStations_reversed.add(new BeauB().addStation());
        allLRTStations_reversed.add(new Vander().addStation());
        allLRTStations_reversed.add(new RoseHill().addStation());
        return stations;
    }

    private void setMapPadding (Float offset) {
        //From 0.0 (min) - 1.0 (max)
        // bsExpanded - bsCollapsed;
        Float maxMapPaddingBottom = 1.0f;
        gMap.setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom));
    }

    //bus station_bus markers
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

    private void addalllists () {
        allBusMarkers = new ArrayList<>();
        allBusStations = new ArrayList<>();

        for (int i = 0; i < setBusStations().size(); i++) {
            Station station = setBusStations().get(i);
            //get LatLng for each marker in ArrayList
            allBusStations.add(station);
        }

        allLRTMarkers = new ArrayList<>();
        allLRTStations = new ArrayList<>();

        for (int i = 0; i < setLrtStations().size(); i++) {
            Station station = setLrtStations().get(i);
            if (station != null) {
                allLRTStations.add(station);
            } else
                Log.e("its", null);
        }
    }

    static void hideAll () {
        for (Marker marker: allMarkers) {
            marker.setVisible(!marker.isVisible());
        }
    }

    GoogleMap.OnGroundOverlayClickListener listener_g = new GoogleMap.OnGroundOverlayClickListener() {
        @Override
        public void onGroundOverlayClick(@NonNull GroundOverlay groundOverlay) {
            Log.e("position", groundOverlay.getPosition() + "");
        }
    };

    static ArrayList<Station> setBusStations () {
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

    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        com.example.fypmetroapp.LatLng now = new com.example.fypmetroapp.LatLng(location.getLatitude(), location.getLongitude());
        if (role != null) {
            if (role.equals("User")) {
                if (gMap != null) {
                    uid = firebaseAuth.getCurrentUser().getUid();
                    userUpdates.setLocation(location);
                    userUpdates.setLatLngLocation(latLng);
                    if (userMarker != null) userMarker.remove();

                    userMarker = gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(),
                                    location.getLongitude()))
                            .visible(false)
                            .title(uid));
                    ShowNearestStations();
                    gMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(userMarker.getPosition(), 13.0f));
                }
            }
            else if (role.equals("Driver")) {
                if (gMap != null) {
                    Bus_DriverUpdates bus_updates = HomeFragment_Driver.bus_updates;
                    LRV_DriverUpdates lrv_updates = HomeFragment_Driver.lrv_updates;
                    if (bus_updates.bus_name != null) {
                        if (bus_updates.tracking) {
                            driverRef = FirebaseDatabase.getInstance().getReference("tracking").child(bus_updates.type).child(bus_updates.bus_name);
                            HomeFragment_Driver.send_bus_location(location, bus_updates.bus_name, driverRef);
                            driverGeoFire.setLocation(bus_updates.bus_name, new GeoLocation(
                                    location.getLatitude(),
                                    location.getLongitude()), (key, error) -> {
                                if (driverMarker != null) driverMarker.remove();

                                driverMarker = gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(),
                                                location.getLongitude()))
                                        .visible(true)
                                        .title(bus_updates.bus_name));
                            });
                        }
                    }
                    else if (lrv_updates.lrv_name != null) {
                        if (lrv_updates.tracking) {
                            driverRef = FirebaseDatabase.getInstance().getReference("tracking").child(lrv_updates.type).child(lrv_updates.lrv_name);
                            HomeFragment_Driver.send_lrv_location(location, lrv_updates.lrv_name, driverRef);
                            driverGeoFire.setLocation(lrv_updates.lrv_name, new GeoLocation(
                                    location.getLatitude(),
                                    location.getLongitude()), (key, error) -> {
                                if (driverMarker != null) driverMarker.remove();

                                driverMarker = gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getLatitude(),
                                                location.getLongitude()))
                                        .visible(true)
                                        .title(lrv_updates.lrv_name));
                            });
                        }
                    }
                }
            }
        }
        tinyDB.putObject("location", now);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        locationManager.requestLocationUpdates(provider, 10000, 150, this);
        Log.e("started", "start");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        tinyDB.registerOnSharedPreferenceChangeListener(listener_db);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}