//package com.example.fypmetroapp;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.content.pm.ProviderInfo;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.slidingpanelayout.widget.SlidingPaneLayout;
//
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.common.internal.Constants;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.UiSettings;
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.MapStyleOptions;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.security.ProviderInstaller;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
//import com.google.android.material.bottomappbar.BottomAppBar;
//import com.google.android.material.bottomsheet.BottomSheetBehavior;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.security.Provider;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class MapsFragment extends Fragment implements OnMapReadyCallback,
//        GoogleMap.OnMyLocationClickListener,
//        GoogleMap.OnMyLocationButtonClickListener,
//        ActivityCompat.OnRequestPermissionsResultCallback ,
//        GoogleMap.OnMarkerClickListener {
//
//    private GoogleMap gMap;
//    private static final String TAG = MainLocationView.class.getSimpleName();
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private boolean permissionDenied = false;
//    private View locationButton;
//    private AnchorSheetBehavior bsBehavior;
//    RelativeLayout tapactionlayout;
//    View bottomSheet;
//    private LatLng mLoc;
//
//    public MapsFragment() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        Log.e("oncreateView", "view creating!");
//        return inflater.inflate(R.layout.fragment_maps2, container, false);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        onResume();
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMapFrag);
//        mapFragment.getMapAsync(this);
//        Log.e("onViewCreated", "view created!");
//
//        tapactionlayout = getView().findViewById(R.id.tap_action_layout);
//
//        tapactionlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bsBehavior.getState()==AnchorSheetBehavior.STATE_COLLAPSED) {
//                    bsBehavior.setState(AnchorSheetBehavior.STATE_ANCHOR);
//                }
//            }
//        });
//
//        bottomSheet = getView().findViewById(R.id.bottomsheet_map);
//
//        bsBehavior = AnchorSheetBehavior.from(bottomSheet);
//        bsBehavior.setState(AnchorSheetBehavior.STATE_COLLAPSED);
//
//        //anchor offset. any value between 0 and 1 depending upon the position u want
//        bsBehavior.setAnchorOffset(0.5f);
//        bsBehavior.setAnchorSheetCallback(new AnchorSheetBehavior.AnchorSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == AnchorSheetBehavior.STATE_COLLAPSED) {
//                    //action if needed
//                }
//
//                if (newState == AnchorSheetBehavior.STATE_EXPANDED) {
//
//                }
//
//                if (newState == AnchorSheetBehavior.STATE_DRAGGING) {
//
//                }
//
//                if (newState == AnchorSheetBehavior.STATE_ANCHOR) {
//
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//                float h = bottomSheet.getHeight();
//                float off = h*slideOffset;
//
//                switch (bsBehavior.getState()) {
//                    case AnchorSheetBehavior.STATE_DRAGGING:
//                        gMap.setPadding(0,10,0,0);
//                        //reposition marker at the center
//                        if (mLoc != null) gMap.moveCamera(CameraUpdateFactory.newLatLng(mLoc));
//                        break;
//                    case AnchorSheetBehavior.STATE_SETTLING:
//                        gMap.setPadding(0,20,0,0);
//                        //reposition marker at the center
//                        if (mLoc != null) gMap.moveCamera(CameraUpdateFactory.newLatLng(mLoc));
//                        break;
//                    case AnchorSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case AnchorSheetBehavior.STATE_EXPANDED:
//                        break;
//                    case AnchorSheetBehavior.STATE_COLLAPSED:
//                        break;
//                }
//            }
//        });
//    }
//
//    public void setUpMapIfNeeded() {
//
////        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
////        mapFragment.getMapAsync(new OnMapReadyCallback() {
////            @Override
////            public void onMapReady(GoogleMap googleMap) {
////                gMap = googleMap;
////
////                //set an initial location and add a marker
////                onMyLocationButtonClick();
////            }
////        });
//    }
//
//    private void setMapPaddingBotttom(Float offset) {
//        //From 0.0 (min) - 1.0 (max) // bsExpanded - bsCollapsed;
//        Float maxMapPaddingBottom = 1.0f;
//        gMap.setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom));
//
//    }
//
//    @Override
//    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
//    @SuppressLint({"MissingPermission", "ResourceType"})
//    public void onMapReady(GoogleMap googleMap) {
//        //initialise map for use
//        gMap = googleMap;
//        addRailStations();
//        Log.e("onmapready", "map initialised!");
//
//        LatLngBounds mauritius = new LatLngBounds(
//                new LatLng(-20.523707, 57.277314),
//                new LatLng(-20.000119, 57.847562)
//        );
//
//        //set map type to normal
//        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        //map zoom limits
//        gMap.setMaxZoomPreference(19.0f);
//        gMap.setMinZoomPreference(13.0f);
//
//        /*
//         * AUTO COMPLETE SEARCH BAR */
//        // Initialize the SDK
//        Places.initialize(getActivity(), "AIzaSyAYWL_z-TJOBptGAzkXbVB2ZE_NhP27Yx4");
//        //Places.initialize(getApplicationContext(), "AIzaSyAYWL_z-TJOBptGAzkXbVB2ZE_NhP27Yx4");
//        Log.e("assigned", "initialise found!");
//
//        // Create a new PlacesClient instance
//        PlacesClient placesClient = Places.createClient(getActivity());
//        //PlacesClient placesClient = Places.createClient(this);
//        Log.e("assigned", "places found!");
//
//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        Log.e("assigned", "frag found!");
//
//        //TODO: autocomplete search customiser
//        autocompleteFragment.setCountry("MU");
//        autocompleteFragment.setHint("Where to today?");
//        ((EditText)getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(14.0f);
//        ImageView ivSearch = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button);
//        ivSearch.setBackgroundColor(Color.BLACK);
//        ivSearch.setVisibility(View.GONE);
//
//
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NotNull Place place) {
//                gMap.clear();
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//
//                //add a marker on map
//                gMap.addMarker(new MarkerOptions()
//                        //set marker position to place LatLng
//                        .position(place.getLatLng())
//
//                        //set marker name to selected place
//                        .title(place.getName())
//                );
//
//                //smooth move camera to selected place
//                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f));
//
//                UiSettings uiSettings = gMap.getUiSettings();
//                //disable marker toolbar on click
//                uiSettings.setMapToolbarEnabled(false);
//            }
//
//
//            @Override
//            public void onError(@NotNull Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//
//        Log.e("auto", "auto set!");
//
//
//        try {
//            /*
//             Customise the styling of the base map using a JSON object defined
//             in a raw resource file.
//            */
//            boolean success = gMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            //location of my json styles
//                            getActivity(), R.raw.style_json));
//
//            if (!success) {
//                Log.e(TAG, "Failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "No style found. Error: ", e);
//        }
//
//        Log.e("jsons", "json done!");
//
//        gMap.setOnMyLocationButtonClickListener(MapsFragment.this);
//        gMap.setOnMyLocationClickListener(MapsFragment.this);
//        gMap.setOnMarkerClickListener(MapsFragment.this);
//        Log.e("location", "location enabled!");
//
//        //call location before doing anything with it
//        enableMyLocation();
//
//        //initialise ImageView
//        locationButton = (ImageView) getView().findViewById(0x2);
//
//        // Change the visibility of my location button
//        if(locationButton != null)
//            locationButton.setVisibility(View.GONE);
//
//        //implement listener for ImageView
//        getView().findViewById(R.id.imMyLocation).setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//            @Override
//            public void onClick(View v) {
//                if(gMap != null)
//                {
//                    if(locationButton != null)
//                        //clicking ImageView calls Google's location button which is hidden
//                        locationButton.callOnClick();
//                }
//            }
//        });
//
//        Log.e("added", "stations added!");
//        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//
//                //gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mauritius, 30));
//                Log.e("bound", "camera bound!");
//                gMap.setLatLngBoundsForCameraTarget(mauritius);
//                Log.e("set bounds", "bounds done!");
//            }
//        });
//    }
//
//    /**
//     * Enables the My Location layer if the fine location permission has been granted.
//     */
//    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (gMap != null) {
//                gMap.setMyLocationEnabled(true);
//
//                //on load pan camera to user's location
//                LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                Criteria mCriteria = new Criteria();
//                String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
//                Location mLocation = manager.getLastKnownLocation(bestProvider);
//                if (mLocation != null) {
//                    Log.e("TAG", "GPS is on");
//                    final double currentLatitude = mLocation.getLatitude();
//                    final double currentLongitude = mLocation.getLongitude();
//                    LatLng loc1 = new LatLng(currentLatitude, currentLongitude);
//                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
//                    gMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//                }
//            }
//        } else {
//            // Permission to access the location is missing. Show rationale and request permission
//            PermissionUtils.requestPermission(((AppCompatActivity) getActivity()), LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//        }
//    }
//
//    //TODO:IMPLEMENT MARKER CLICKING
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        return false;
//    }
//
//    //NOT NECESSARY
//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        gMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
//        return false;
//    }
//
//    //TODO: ADD RAILSTATIONS WITH LATLNG
//    //add LRT station markers to map
//    private void addRailStations() {
//        //custom marker size
//        int height = 100;
//        int width = 100;
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.train_station);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
//
//        for (int i = 0; i < createSTitle().size(); i++){
//            //get LatLng for each marker in ArrayList
//            LatLng markerPos = new LatLng(getLat(i), getLat(i));
//            gMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(getLat(i), getLng(i)))
//                    .icon(smallMarkerIcon)
//                    .title(getSTitle(i))
//                    .flat(true)
//                    .zIndex(5.0f)
//            );
//        }
//    }
//
//    //ArrayList for marker Latitudes
//    private ArrayList<Double> createSLat () {
//        ArrayList<Double> stationsLat = new ArrayList<>();
//        stationsLat.add(-20.2421818);
//        stationsLat.add(-20.2354926);
//        stationsLat.add(-20.2266891);
//        stationsLat.add(-20.2209104);
//        stationsLat.add(-20.1837264);
//        stationsLat.add(-20.180942);
//        stationsLat.add(-20.1625125);
//
//        return stationsLat;
//    }
//
//    //ArrayList for marker Longitudes
//    private ArrayList<Double> createSLng () {
//        ArrayList<Double> stationsLng = new ArrayList<>();
//        stationsLng.add(57.4758875);
//        stationsLng.add(57.473157);
//        stationsLng.add(57.4673957);
//        stationsLng.add(57.4584639);
//        stationsLng.add(57.4693912);
//        stationsLng.add(57.4767888);
//        stationsLng.add(57.4982089);
//
//        return stationsLng;
//    }
//
//    //ArrayList for marker Titles
//    private ArrayList<String> createSTitle () {
//        ArrayList<String> stationsTitles = new ArrayList<>();
//        stationsTitles.add("Rose Hill Central Station");
//        stationsTitles.add("Vandermeesch");
//        stationsTitles.add("Beau Bassin");
//        stationsTitles.add("Barkly");
//        stationsTitles.add("Coromandel");
//        stationsTitles.add("St Louis");
//        stationsTitles.add("Port Louis Victoria");
//
//        return stationsTitles;
//    }
//
//    //retrieve marker title from ArrayList at index
//    public String getSTitle (int i) {
//        ArrayList<String> statTitle = createSTitle();
//        return statTitle.get(i);
//    }
//
//    //retrieve marker Latitude from ArrayList at index
//    public double getLat (int i) {
//        ArrayList<Double> statLat = createSLat();
//        return statLat.get(i);
//    }
//
//    //retrieve marker Longitude from ArrayList at index
//    public double getLng (int i) {
//        ArrayList<Double> statLng = createSLng();
//        return statLng.get(i);
//    }
//
//    //TODO: when user clicks blue circle location icon
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return;
//        }
//
//        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        } else {
//            // Permission was denied. Display an error message
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true;
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.e("resume", "on resume called");
//        if (permissionDenied) {
//            // Permission was not granted, display error dialog.
//            showMissingPermissionError();
//            permissionDenied = false;
//        }
//    }
//
//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getFragmentManager(), "dialog");
//    }
//}