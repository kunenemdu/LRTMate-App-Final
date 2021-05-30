package com.example.fypmetroapp;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.green;

public class Maps_Full_Access extends Fragment implements GeoQueryDataEventListener, LocationListener {

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
    LatLng curLocationLatLng;
    Location curLocation;
    TextView textAddress;
    LinearLayout llNearbySheet;
    LinearLayout llBus_Stations_Route;
    LinearLayout llFollow_Route;
    LinearLayout llBottomSheet;
    LinearLayout llLRT_StationSheet_Sche;
    LinearLayout llBUS_StationSheet_Sche;
    RelativeLayout rlDirections;
    private Polyline polyline_LRT;
    private Polyline polyline_BUS;
    private Polyline polyline_Directions;
    BottomSheetBehavior bottomSheetBehavior_NearBy;
    BottomSheetBehavior bottomSheetBehavior_Buses_Stations_Route;
    BottomSheetBehavior bottomSheetBehavior_Follow_Route;
    BottomSheetBehavior bottomSheetBehavior_Directions;
    BottomSheetBehavior bottomSheetBehavior_LRT_ClickedStation_Sche;
    FloatingActionButton fabDirections;
    LocationCallback locationCallback;
    ArrayList<Marker> allLRTMarkers;
    ArrayList<Marker> allBusMarkers;
    ArrayList<Marker> allMarkers;
    ArrayList<Station> allBusStations, allLRTStations, allStations;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    DatabaseReference userRef;
    DatabaseReference driverRef;
    TextView timeText;
    TextView directionText;
    TextView busText, busAtStation;
    TextView clickedBus;
    ImageButton directionsButton, fav_to, fav_from;
    public static final int overview = 0;
    CardView nearbyCardView;
    CardView drivingMode;
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
    private Dialog stationDetailsDialog;
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
    SupportMapFragment supportMapFragment;
    private ClusterManager<MarkerClusterItem> clusterManager;
    MarkerClusterRenderer<MarkerClusterItem> clusterRenderer;
    RoutingApi onlineRoutingApi;
    ArrayList<Map<String, String>> favourites;
    Set<String> favourite;
    MaterialSearchBar materialSearchBar;
    PlacesClient placesClient;
    List<AutocompletePrediction> predlist;

    //Class Declarations
    Origin origin = new Origin();
    Destination destination = new Destination();
    UserUpdates userUpdates = new UserUpdates();
    SearchPlace searchPlace = new SearchPlace();

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
        favourites = new ArrayList<Map<String, String>>();
        active = this;
        foundfences = new ArrayList<>();
        fabDirections = getView().findViewById(R.id.fab_directions);
        fabDirections.hide();
        textAddress = getView().findViewById(R.id.textAddress);
        titleText = getView().findViewById(R.id.titleText);
        txtClicked_LRT_Station = getView().findViewById(R.id.txtSelectedtation);
        timeText = null;
        directionText = null;
        nearbyCardView = getView().findViewById(R.id.nearbystations);
        directionsButton = getView().findViewById(R.id.startDirections);
        fav_from = getView().findViewById(R.id.fav_from);
        fav_to = getView().findViewById(R.id.fav_to);
        drivingMode = getView().findViewById(R.id.drivingMode);
        stationDetailsDialog = new Dialog(getContext());
        onlineRoutingApi = OnlineRoutingApi.create(getContext(), Constants.APIKEY);
        rlDirections = getView().findViewById(R.id.directions_frag);
        materialSearchBar = getView().findViewById(R.id.searchBar);
        Places.initialize(this.getContext(), Config.MYAPI_KEY);
        placesClient = Places.createClient(getContext());
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
                        Log.e("failed", "its null");
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        directionsButton.setOnClickListener(Buttons);
        fav_from.setOnClickListener(Buttons);
        fav_to.setOnClickListener(Buttons);
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
        initAutoFrags();
        initClicked_BUS_Component();
        initFollowRoute_Component();
        clickedBus = getView().findViewById(R.id.clickedBus);
        //supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMapFrags);
        //supportMapFragment.getMapAsync(this::onMapReady);
    }

    //update user's favourite locations
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            HomeFragment_User.update_favourites();
        }
    };

    private void buildLocationRequest() {
        locationRequest = LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(500000)
                .setFastestInterval(250000)
                .setSmallestDisplacement(100f);
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
        LRT_Polyline();
        addRailStations();
        addBusStations();

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Marker clicked = gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(latLng.toString()));
            }
        });

        //TODO: MAP CLICK LISTENER
        gMap.setOnMapClickListener(latLng -> {
            removeLRTPoly();
            removeBUSPoly();
            removeDirectionsPoly();
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

            if (bottomSheetBehavior_Follow_Route.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_COLLAPSED);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (bottomSheetBehavior_Follow_Route.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_HIDDEN);
                nearbyCardView.setVisibility(View.VISIBLE);
            }

            if (nearbyCardView.getVisibility() == View.INVISIBLE)
                nearbyCardView.setVisibility(View.VISIBLE);

        });
        if (getPolyline_LRT() != null)
            getPolyline_LRT().setVisible(false);

        addBuses();

        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterRenderer = new MarkerClusterRenderer<>(getContext(), googleMap, clusterManager);
        setupClusterManager();
        tester();
        allStations = new ArrayList<>();
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
        MarkerClusterRenderer<MarkerClusterItem> clusterRenderer = new MarkerClusterRenderer(getContext(), gMap, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
    }

    private void setupClusterManager() {
        addClusterItems();
        setClusterManagerClickListener();
        setRenderer();
        clusterManager.cluster();
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

    private void initStationsFences() {
        stationsFences = new ArrayList<>();

        for (Marker this_marker: getAllMarkers()){
            LatLng statLoc = new LatLng(this_marker.getPosition().latitude, this_marker.getPosition().longitude);
            String statName = this_marker.getTitle();
            String type = (String) this_marker.getTag();
            int distance = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, statLoc);

            //instantiate the station GeoFence
            StationFence stationFence = new StationFence(statName, statLoc, type, distance);
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
                    .alternatives(true)
                    .await();
        } catch (ApiException | IOException | InterruptedException e) {
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
                .setQueryRateLimit(3)
                .setApiKey(Config.MYAPI_KEY)
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setReadTimeout(10, TimeUnit.SECONDS)
                .setWriteTimeout(10, TimeUnit.SECONDS);
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
                nearbyCardView.setVisibility(View.INVISIBLE);
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                LatLng latLng = place.getLatLng();
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
                bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                LatLng latLng = place.getLatLng();
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                String user_origin = temp.getLatitude() + "," + temp.getLongitude();
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

    @SuppressLint("NewApi")
    private void favourite_to () {
        favourite = new HashSet<String>();
        favourite.add(destination.getName() + " ");
        favourite.add(destination.getDestination());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("first", favourite);
        editor.commit();
    }

    @SuppressLint("NewApi")
    private void favourite_from () {
        favourite = new HashSet<String>();
        favourite.add(origin.getName() + " ");
        favourite.add(origin.getOrigin());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("second", favourite);
        editor.commit();
    }

    //event listener for buttons
    android.view.View.OnClickListener Buttons = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.fav_to:
                    favourite_to();
                    break;
                case R.id.fav_from:
                    favourite_from();
                    break;
                case R.id.startDirections:
                    rlDirections.setVisibility(View.VISIBLE);
                    break;
                case R.id.nearbystations:
                    ShowNearestStations();
                    hideAllSheets();
                    break;
                case R.id.drivingMode:
                    doDrivingInstructions();
                    break;
            }
        }
    };

    private void hideAllSheets () {
        if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_HIDDEN)
            bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (bottomSheetBehavior_NearBy.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        if (rlDirections.getVisibility() == View.VISIBLE) {
            nearbyCardView.setVisibility(View.INVISIBLE);
            rlDirections.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    private void doDrivingInstructions () {
        DirectionsResult results = getDirectionsDetails(origin.getOrigin(), destination.getDestination(), TravelMode.WALKING);
        if (results != null) {
            int num = results.routes[0].legs[0].steps.length;
            ArrayList<String> instructions = new ArrayList<>();
            try {
                for (int i = 0; i < num; i++) {
                    String get = results.routes[0].legs[0].steps[i].htmlInstructions;
                    String instruction = String.valueOf(Html.fromHtml(get, Html.FROM_HTML_MODE_COMPACT));
                    instructions.add(instruction);
                }
                ShowFollowRoute(instructions);
                addWalkToBusPolyline(results, gMap);
                positionCamera(results.routes[overview], gMap);
            }
            catch (Exception e) {
                Log.e("error here", e.getMessage());
            }
            addPolyline(results, gMap);
            positionCamera(results.routes[overview], gMap);
            walkToStation();
            addMarkersToMap(results, gMap);
            bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetBehavior_NearBy.setState(BottomSheetBehavior.STATE_HIDDEN);
            nearbyCardView.setVisibility(View.INVISIBLE);
            bottomSheetBehavior_Follow_Route.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            rlDirections.setVisibility(View.INVISIBLE);
        }
        ArrayList<Station> stations_to_dest = new ArrayList<>();
        gMap.setTrafficEnabled(false);

        for (Station station: allLRTStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(station.getPosition(), destination.getPosition());
            if (distance < 1000) {
                stations_to_dest.add(station);
                Log.e("Station lrt:", station.name);
            }
        }

        for (Station station: allBusStations) {
            int distance = (int) SphericalUtil.computeDistanceBetween(station.getPosition(), destination.getPosition());
            if (distance < 1000) {
                //14min = 1km
                stations_to_dest.add(station);
                Log.e("Station bus:", station.name);
            }
        }
    }

    private void initMarkerSheet () {
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
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fabDirections.hide();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
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
        if (userUpdates.getLatLngLocation() != null) {
            int dist = (int) SphericalUtil.computeDistanceBetween(userUpdates.getLatLngLocation(), searchPlace.getPosition());
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
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nearbyCardView.setVisibility(View.INVISIBLE);
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

                switch (bottomSheetBehavior_Follow_Route.getState()) {
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
        int distance = 0;

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
            if (distance <= 1000)
                nearbyStations.add(LRTstation);
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

            LayoutInflater inflater = Maps_Full_Access.this.getLayoutInflater();
            TableRow mainRow = new TableRow(Maps_Full_Access.this.getContext());

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
            }
            else if (station.type.equals(Config.STATION_TYPE_BUS)) {
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

    private boolean onMarkerClick(Marker marker) {
        try {
            if (marker.getTag().equals("P")) {
                titleText.setText(marker.getTitle());
                textAddress.setText(marker.getPosition().toString());
                //show it as collapsed
                bottomSheetBehavior_Directions.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                //show clicked station sheet as half expanded
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
            }
            else if (marker.getTag().equals("D")) marker.showInfoWindow();
            else if (marker.getTag().equals("O")) marker.showInfoWindow();

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
            stationDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
            ImageButton closeDialog = stationDetailsDialog.findViewById(R.id.dialog_closeX);
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

        //inflate each LRT station to view
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

    //show the directions for a bus route
    @SuppressLint("NewApi")
    private void ShowFollowRoute(ArrayList<String> instructions) {
        try {
            TableLayout mainTable_stations = getView().findViewById(R.id.mainTable_Route_follow);
            mainTable_stations.removeAllViews();
            TextView tv;
            for (int y = 0; y < instructions.size(); y++) {
                LayoutInflater inflater = Maps_Full_Access.this.getLayoutInflater();
                TableRow mainRow = new TableRow(Maps_Full_Access.this.getContext());
                String instruction = instructions.get(y);

                if (instruction.contains("north"))
                    inflater.inflate(R.layout.follow_north, mainRow);

                else if (instruction.contains("southeast"))
                    inflater.inflate(R.layout.follow_southeast, mainRow);

                else if (instruction.contains("southwest"))
                    inflater.inflate(R.layout.follow_southwest, mainRow);

                else if (instruction.contains("Slight left"))
                    inflater.inflate(R.layout.follow_slight_left, mainRow);

                else if (instruction.contains("Slight right"))
                    inflater.inflate(R.layout.follow_slight_right, mainRow);

                else if (instruction.contains("right"))
                    inflater.inflate(R.layout.route_instruction_right, mainRow);

                else if (instruction.contains("left"))
                    inflater.inflate(R.layout.follow_left, mainRow);

                tv = new TextView(mainRow.getContext());
                tv.setText(instruction);
                tv.setTextSize(14.0f);
                mainRow.addView(tv);
                mainTable_stations.addView(mainRow);
            }
        }
        catch (Exception e) {
            Log.e("clapped", "error!");
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

    public void removeDirectionsPoly() {
        //remove whole bus POLYLINE
        if (route != null) {
            for (Polyline line : route) {
                line.remove();
            }
            route.clear();
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
                            ShowRouteStations(thisBus);
                            new Bus().busRoute(thisBus);
                            stationDetailsDialog.dismiss();
                            bottomSheetBehavior_Buses_Stations_Route.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        }
                    }
                });
            }

            //SnackBar tip component
            /*Snackbar.make(table_buses, "Click a bus too see its route!",
                    Snackbar.LENGTH_SHORT)
                    .show();*/

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
                gMap.setTrafficEnabled(true);

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

    private void addAllStations () {
        addRailStations();
        addBusStations();
        for (Station station: allBusStations)
            allStations.add(station);
        for (Station station: allLRTStations)
            allStations.add(station);
    }

    //TODO: CHANGE IMPLEMENTATION OF LRT STATIONS
    private ArrayList<Station> setLrtStations () {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(new RoseHill().addStation());
        stations.add(new Vander().addStation());
        stations.add(new BeauB().addStation());
        stations.add(new Barkly().addStation());
        stations.add(new Corom().addStation());
        stations.add(new StLouis().addStation());
        stations.add(new PortLouis().addStation());
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

    @SuppressLint("NewApi")
    private void walkToStation () {
        if (userUpdates.location != null) {
            Station station = userUpdates.getNearest_station();
            curLocationLatLng = new LatLng(userUpdates.location.getLatitude(), userUpdates.location.getLongitude());
            distance = (int) SphericalUtil.computeDistanceBetween(curLocationLatLng, station.getPosition());

            if (distance < 50) {

                if (station.type.equals("BUS")) {
                    String origin = UserUpdates.location.getLatitude() + "," + UserUpdates.location.getLongitude();
                    String destination = station.position.latitude + "," + station.position.longitude;

                    DirectionsResult results = getWalkingDetails(origin, destination, TravelMode.WALKING);
                }
            }
        }
    }

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        //sendNotif("LRTMate App", String.format("%s entered the station.", dataSnapshot.getKey()));
        Log.e("entered", dataSnapshot.getKey());
        LatLng loc = new LatLng(location.latitude, location.longitude);
        if (location != null) {
            if (!loc.equals(userUpdates.getLatLngLocation())) {
                Log.e("entered new", dataSnapshot.getKey());
                entered_new_Station(location);
            } else {
                Log.e("entered", "same");
                entered_same_Station(location);
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

        //decrementCounter(reference, userUpdates.getPrevious_stat().name);
        Log.e("user left", UserUpdates.nearest_station.name);
    }

    //pinpoint the user when entering a trigger
    private boolean entered_new_Station(GeoLocation location) {
        _approaching = new ArrayList<>();
        boolean found = true;
        LatLng user = new LatLng(location.latitude, location.longitude);
        Station station = new Station();
        String newStation = null;
        int nearest_station = 0;
        int dist_to = 0;

        if (!user.equals(curLocationLatLng)) {
            //HomeFragment_User.init.setText("Location Changed. Refreshing UI...");
        }

        for (StationFence stationFence : getStationsFences()) {
            //Log.e("stat", stationFence.stationName);
            //Log.e("dist", String.valueOf(stationFence.distance));
            //nearest_station = (int) SphericalUtil.computeDistanceBetween(user, stationFence.stationLocation);
            //Log.e("far", String.valueOf(nearest_station));
            //Log.e("name", String.valueOf(stationFence.stationName));
            //if user becomes <30m away from a station
            if (stationFence.distance <= Config.ALLOWED_PROXIMITY) {
                dist_to = nearest_station;
                if (!foundfences.contains(stationFence)) {
                    foundfences.add(stationFence);
                }

                if ((stationFence.distance > Config.ACCURACY_DISTANCE)) {
                    //Log.e("entered", "poss");
                    //if they are close enough <50m
                    station.name = (stationFence.stationName);
                    station.type = (stationFence.type);
                    station.position = (stationFence.stationLocation);
                    station.distance = (nearest_station);
                    userUpdates.setNearest_station(station);
                    userUpdates.setDistance_to_nearest_station(stationFence.distance);
                    //Log.e("set poss", "station");

                    newStation = station.name;
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    CollectionReference reference = firebaseFirestore.collection("stationdetails");
                    //incrementCounter(reference, newStation);
                    //Log.e("poss dist to", station.name + " " + dist_to);
                    found = false;
                    return found;
                }
                //if user becomes <25m away from a station
                else if (stationFence.distance <= Config.ACCURACY_DISTANCE) {
                    //Log.e("entered", "closer");

                    station.name = (stationFence.stationName);
                    station.type = (stationFence.type);
                    station.position = (stationFence.stationLocation);
                    station.distance = (stationFence.distance);
                    userUpdates.setNearest_station(station);
                    userUpdates.setDistance_to_nearest_station(stationFence.distance);
                    newStation = stationFence.stationName;
                    Log.e("set", newStation);


                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    CollectionReference reference = firebaseFirestore.collection("stationdetails");
                    incrementCounter(reference, newStation);
                    Log.e("accu dist to", userUpdates.getNearest_station().name + " " + userUpdates.getDistance_to_nearest_station());
                    found = false;
                    return found;
                }
            }
        }
        return found;
    }

    private void entered_same_Station (GeoLocation location) {
        LatLng user = new LatLng(location.latitude, location.longitude);
        boolean found = false;
        Station station = new Station();
        String newStation = null;
        int nearest_station = 0;
        int dist_to = 0;

        if (!user.equals(curLocationLatLng)) {
            HomeFragment_User.init.setText("Location Changed. Refreshing UI...");
        }

        while (found == false) {
            for (StationFence stationFence : getStationsFences()) {
                //Log.e("stat", stationFence.stationName);
                //Log.e("dist", String.valueOf(stationFence.distance));
                //nearest_station = (int) SphericalUtil.computeDistanceBetween(user, stationFence.stationLocation);
                //Log.e("far", String.valueOf(nearest_station));
                //Log.e("name", String.valueOf(stationFence.stationName));
                //if user becomes <30m away from a station
                if (stationFence.distance <= Config.ALLOWED_PROXIMITY) {
                    if (!foundfences.contains(stationFence)) {
                        foundfences.add(stationFence);
                    }

                    if ((stationFence.distance > Config.ACCURACY_DISTANCE)) {
                        //Log.e("entered", "poss");
                        //if they are close enough <50m
                        station.name = (stationFence.stationName);
                        station.type = (stationFence.type);
                        station.position = (stationFence.stationLocation);
                        station.distance = (nearest_station);
                        userUpdates.setNearest_station(station);
                        userUpdates.setDistance_to_nearest_station(stationFence.distance);
                        UserUpdates.distance_to_nearest_station = stationFence.distance;
                        //Log.e("set poss", "station");

                        newStation = station.name;
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        CollectionReference reference = firebaseFirestore.collection("stationdetails");
                        //incrementCounter(reference, newStation);
                        //Log.e("poss dist to", station.name + " " + dist_to);
                        found = true;
                        break;
                    }
                    //if user becomes <25m away from a station
                    else if (stationFence.distance <= Config.ACCURACY_DISTANCE) {
                        //Log.e("entered", "closer");

                        station.name = (stationFence.stationName);
                        station.type = (stationFence.type);
                        station.position = (stationFence.stationLocation);
                        station.distance = (stationFence.distance);
                        userUpdates.setNearest_station(station);
                        userUpdates.setDistance_to_nearest_station(stationFence.distance);
                        UserUpdates.distance_to_nearest_station = stationFence.distance;
                        newStation = stationFence.stationName;
                        Log.e("set", newStation);


                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        CollectionReference reference = firebaseFirestore.collection("stationdetails");
                        incrementCounter(reference, newStation);
                        Log.e("accu dist to", station.name + " " + stationFence.distance);
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

    @SuppressLint("NewApi")
    public boolean incrementCounter(final CollectionReference ref, String name) {
        UserUpdates.updatedOcc = false;
        DocumentReference occRef = ref.document("occupancy");
        String previous_stat = preferences.getString("previous", null);
        if (previous_stat == null) {
            Log.e("started journey at", name);
            /*occRef.update(name, FieldValue.increment(1))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> Log.e("updated", "occupancy for new!"));
            UserUpdates.updatedOcc = true;
            userUpdates.setPrevious_stat(userUpdates.getNearest_station());
            userUpdates.setDistance_to_prev_station(userUpdates.getDistance_to_nearest_station());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("previous", userUpdates.getPrevious_stat().name);
            editor.apply();

            getCount(occRef, name);*/
            return false;
        } else {
            /*if (!UserUpdates.nearest_station.name.equals(previous_stat)) {
                Log.e("changed stations to", name);
                if (UserUpdates.updatedOcc == false) {
                    occRef.update(name, FieldValue.increment(1))
                            .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(aVoid -> Log.e("updated", "occupancy!"));
                    userUpdates.setPrevious_stat(userUpdates.getNearest_station());
                    userUpdates.setDistance_to_prev_station(userUpdates.getDistance_to_nearest_station());
                    UserUpdates.updatedOcc = true;
                    getCount(occRef, name);
                }
            } else {
                Log.e("user is still at", name);
                userUpdates.setPrevious_stat(userUpdates.getNearest_station());
                userUpdates.setDistance_to_prev_station(userUpdates.getDistance_to_nearest_station());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("previous", userUpdates.getPrevious_stat().name);
                editor.apply();
            }*/
            return false;
        }
    }

    public Task<Integer> getCount(final DocumentReference ref, String stationName) {
        // Sum the count of each shard in the subcollection
        //DocumentReference doc = ref.collection("stationdetails").document("occupancy");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                        Log.e("occupied by", String.valueOf(UserUpdates.cur_stat_occupancy));
                } else {
                    Log.e(TAG, "No such document");
                }
            } else {
                Log.e(TAG, "get failed with ", task.getException());
            }
        });

        ref.addSnapshotListener((snapshot, error) -> {
            UserUpdates.cur_stat_occupancy = snapshot.getDouble(stationName);
            Log.e("now occupied by", String.valueOf(UserUpdates.cur_stat_occupancy));
        });
        return null;
    }

    public Task<Void> decrementCounter (final CollectionReference ref, String name) {
        DocumentReference occRef = ref.document("occupancy");
        if (UserUpdates.nearest_station.name != null) {
            if (UserUpdates.updatedOcc == false) {
                occRef.update(name, FieldValue.increment(-1))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> Log.e("decremented", "occupancy!"));
                UserUpdates.updatedOcc = true;
                getCount(occRef, name);
            }
        }
        return null;
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Toast.makeText(getContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void onLocationChanged(@NonNull Location location) {
        ShowNearestStations();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                    query.addGeoQueryDataEventListener(Maps_Full_Access.this);
                }
            });
        }

        Log.e(TAG, "GPS LocationChanged");
        Log.e(TAG, "Received GPS request for " + latLng);
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
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void tester () {
//        for (Bus bus: allBuses) {
//            for (Station station: bus.getStops()) {}
//                Log.e(String.valueOf(bus.getName()), station.name + " at " + station.getPosition());
//        }
        Station dest = new Moka().addStation();
        Station ori = new QB().addStation();
        ArrayList<Station> stats = new ArrayList<>();
        int pos = 0;

        //direct bus routing from stat -> stat
        for (Bus bus: ori.getBuses()) {
            for (Bus bus2 : dest.getBuses()) {
                if (bus.getName() == (bus2.getName())) {
                    Log.e("should take", "" + bus2.getName());
                    for (Station station: bus2.getStops()) {
                        if (station.getName().equals(dest.getName())) {
                            Log.e("from", ori.getName());
                            Log.e("and get off at", station.getName());
                            //Log.e("from", bus2.getStops().get(bus2.getStops().size()-1).getName());
                        }
                    }
                }
            }
        }
//        for (Bus bus: dest.getConnects_to()) {
//            for (Bus bus_qb: qb.getConnects_to()) {
//                if (bus.equals(bus_qb)) {
//                    Log.e("QB has", String.valueOf(bus_qb.getName()));
//                }
//            }
//        }
        ArrayList<Station> buseshere = new ArrayList<>();
        int ridestops = 0;
        for (Bus bus: dest.getBuses()) {
//            for (Station station: allBusStations) {
//                for (Bus bus1: station.getBuses()) {
//                    Log.e("take", bus1.getName() + " at " + station.getName());
//                    if (dest.getBuses().contains(bus1.getName())) {
//                        Log.e("get off", "at " + station.getName() + bus.getName());
//                    }
//                }
//            }
//            for (Station station: bus.getStops()) {
//                double dist = SphericalUtil.computeDistanceBetween(ori.getPosition(), station.getPosition());
//                Log.e("taking " + bus.getName() + " to", station.name + " " + String.valueOf(dist));
//                if (dist < 100) {
//                    Log.e("should hop on at", station.name + " and take: " + bus.getName());
//                    Log.e("ride ", ridestops + " stops");
//                }
//                else {
//                    buseshere.clear();
//                    buseshere.add(bus.getStops().get(bus.getStops().size()-1));
//                }
//            }
        }

        Station start = new RoseHill().addStation();
        Location start_loc = create(start.getPosition());
        Station end = new Corom().addStation();
        Location end_loc = create(end.getPosition());

//        for (Station station: allLRTStations) {
//            double dist = SphericalUtil.computeDistanceBetween(start.getPosition(), station.getPosition());
//            Log.e("taking " + start.getName() + " to", station.name + " " + String.valueOf(dist));
//            Log.e("bearing", String.valueOf(start_loc.bearingTo(create(station.getPosition()))));
//        }
    }

    private Location create (LatLng latLng) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLongitude(latLng.longitude);
        location.setLatitude(latLng.latitude);
        return location;
    }
}