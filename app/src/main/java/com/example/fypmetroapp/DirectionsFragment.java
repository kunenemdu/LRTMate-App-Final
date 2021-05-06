package com.example.fypmetroapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DirectionsFragment extends Fragment {

    private static final String TAG = DirectionsFragment.class.getSimpleName() ;
    TextView locText;
    Location mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_directions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //locText = getView().findViewById(R.id.locText);

        /*
         * AUTO COMPLETE SEARCH BAR */
        // Initialize the SDK
        Places.initialize(getContext(), "AIzaSyAYWL_z-TJOBptGAzkXbVB2ZE_NhP27Yx4");
        //Places.initialize(getApplicationContext(), "AIzaSyAYWL_z-TJOBptGAzkXbVB2ZE_NhP27Yx4");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getContext());
        //PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        //TODO: autocomplete search customiser
        autocompleteFragment.setCountry("MU");
        autocompleteFragment.setHint("Where to today?");
        ((EditText) getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(14.0f);
        ImageView ivSearch = autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        //on load pan camera to user's location
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = manager.getLastKnownLocation(bestProvider);

        //display user city on this page
        if (mLocation != null) {
            String userLocation = getAddress(getContext(), mLocation.getLatitude(), mLocation.getLongitude());
            locText.setText(userLocation);
        }
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

                Log.e(TAG, "getAddress:  address" + address);
                Log.e(TAG, "getAddress:  country" + country);
                Log.e(TAG, "getAddress:  city" + city);
                Log.e(TAG, "getAddress:  state" + state);
                Log.e(TAG, "getAddress:  postalCode" + postalCode);
                Log.e(TAG, "getAddress:  knownName" + knownName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userLoc;
    }
}