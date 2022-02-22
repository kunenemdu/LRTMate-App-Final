package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.TRANSPARENT;

public class HomeFragment_User extends Fragment implements ToolTipsManager.TipListener {
    int times_seen = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = HomeFragment_User.class.getSimpleName();
    String user_id;
    UserUpdates userUpdates;
    public static Dialog stationLegendReminder;
    String role;
    static LinearLayout favs, ll_previous, ll_home_sheet, ll_no_previous;
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior_Home_Sheet;
    static RecyclerView previous_trips;
    static ArrayList<Route> routes = new ArrayList<>();
    static ArrayList<Object> routes_objects = new ArrayList<>();
    static Fragment fragment = new HomeFragment_User();
    Dialog dialog;
    static ToolTipsManager manager;
    static RelativeLayout main_container;
    private TinyDB tinyDB = NavigationActivity.tinyDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @SuppressLint("MissingPermission")
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
        // initiate progress bar and start button

        favs = getView().findViewById(R.id.favs);
        ll_previous = getView().findViewById(R.id.prevs);
        ll_no_previous = getView().findViewById(R.id.linear_no_previous);
        previous_trips = getView().findViewById(R.id.previous_trips);
        //Log.e("prefs are", preferences.getAll().toString());
        stationLegendReminder = new Dialog(getActivity());
        main_container = getView().findViewById(R.id.main_container_element);
        initService_Updates();
        Handler legend = new Handler();
        legend.postDelayed(new Runnable() {
            @Override
            public void run() {
                manager = new ToolTipsManager(HomeFragment_User.this);
                boolean legend_seen = NavigationActivity.tinyDB.getBoolean("legend_seen");
                role = NavigationActivity.tinyDB.getString("role");
                update_favourites();
                update_previous();
                times_seen = NavigationActivity.tinyDB.getInt("times_seen");
                //showToolTipsHome();
                if (!role.equals("Driver")) {
                    if (legend_seen == false)
                        showStationsLegend(true);
                    else
                        showStationsLegend(false);
                }

            }
        }, 5000);
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

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        manager = new ToolTipsManager(this);
        //locationManager.requestLocationUpdates(provider, 500, 15, this);
        if (bottomSheetBehavior_Home_Sheet != null)
            bottomSheetBehavior_Home_Sheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        super.onResume();
    }

    @SuppressLint("NewApi")
    private void animateView () {
        ImageButton closeDialog = getView().findViewById(R.id.dialog_closeX);
        ImageView occupancy_anim = getView().findViewById(R.id.occupancy_anim);
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.live_anim);
        // Insert your AnimatedVectorDrawable resource identifier
        occupancy_anim.setImageDrawable(d);

        d.start();
    }

    @SuppressLint("NewApi")
    public static void update_favourites() {
        if (NavigationActivity.tinyDB != null) {
            TextView tv1 = favs.getRootView().findViewById(R.id.second);
            TextView tv2 = favs.getRootView().findViewById(R.id.first);
            if (NavigationActivity.tinyDB.getAll().containsKey("fav1")) {
                Favourite favourite = NavigationActivity.tinyDB.getObject("fav1", Favourite.class);
                tv1.setText(favourite.name);
                tv1.setTextSize(14.5f);
                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(tv1.getContext(), favourite.address, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                tv1.setText("No Address on Record.");
                tv1.setTextSize(14.5f);
            }

            if (NavigationActivity.tinyDB.getAll().containsKey("fav2")) {
                Favourite favourite = NavigationActivity.tinyDB.getObject("fav2", Favourite.class);
                tv2.setText(favourite.name);
                tv2.setTextSize(14.5f);
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(tv2.getContext(), favourite.address, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                tv2.setText("No Address on Record.");
                tv2.setTextSize(14.5f);
            }
        }
    }

    @SuppressLint("NewApi")
    static void update_previous () {
        if (NavigationActivity.tinyDB != null) {
            TextView tv1 = ll_no_previous.findViewById(R.id.no_previous);
            if (NavigationActivity.tinyDB.getAll().containsKey("Previous")) {
                if (tv1.getVisibility() != View.GONE) {
                    tv1.setVisibility(View.GONE);
                    ll_no_previous.setVisibility(View.GONE);
                }
                //tinyDB.remove("Previous");
                if (previous_trips.getVisibility() != View.VISIBLE)
                    previous_trips.setVisibility(View.VISIBLE);

                routes_objects = NavigationActivity.tinyDB.getListObject("Previous", Route.class);
                for(Object objs : routes_objects){
                    routes.add((Route) objs);
                }
                setAdapter_For_Previous(routes, previous_trips.getContext());
            }
            else {
                if (previous_trips.getVisibility() != View.GONE) {
                    previous_trips.setVisibility(View.GONE);
                    if (tv1.getVisibility() != View.VISIBLE)
                        tv1.setVisibility(View.VISIBLE);
                }
            }
        }
        else {
            TextView tv1 = ll_no_previous.findViewById(R.id.no_previous);
            if (previous_trips.getVisibility() != View.GONE) {
                previous_trips.setVisibility(View.GONE);
                if (tv1.getVisibility() != View.VISIBLE)
                    tv1.setVisibility(View.VISIBLE);
            }
        }
    }

    static void update_intervals () {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firebaseFirestore
                .collection("stationdetails")
                .document("arrivals")
                .collection("BUS")
                .document("intervals");

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    double bus_interval_3 = (snapshot.getDouble(String.valueOf(new Bus_3().getName())));
                    double bus_interval_163 = (snapshot.getDouble(String.valueOf(new Bus_163().getName())));
                    double bus_interval_153 = (snapshot.getDouble(String.valueOf(new Bus_153().getName())));

                    NavigationActivity.tinyDB.putDouble("3", bus_interval_3);
                    NavigationActivity.tinyDB.putDouble("163", bus_interval_163);
                    NavigationActivity.tinyDB.putDouble("153", bus_interval_153);
                }
            }
        });

        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.exists()) {
                    update_intervals();
                }
            }
        });
    }

    static void setAdapter_For_Previous (ArrayList<Route> routes, Context context) {
        PreviousAdapter adapter = new PreviousAdapter(routes, context, previous_trips);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        previous_trips.setLayoutManager(manager);
        previous_trips.setItemAnimator(new DefaultItemAnimator());
        previous_trips.setAdapter(adapter);
        if (adapter.getItemCount() >= 4)
            previous_trips.getLayoutParams().height = context.getResources().getDisplayMetrics().heightPixels / 4;
    }

    static void start_routing (Context context, Route route) {
        Journey journey = new Journey();
        journey.setDestination_walk(route.getDestination_walking());
        journey.setOrigin_walk(route.getOrigin_walking());
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Option:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.choose_option);
        arrayAdapter.add("Follow Route Directly From Same Origin");
        arrayAdapter.add("Follow Route From My Current Location");
        arrayAdapter.add("View Route Details");

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
                        MapsFragmentExtras.setRouteAdapter(route, context);
                        NavigationActivity.fm.beginTransaction()
                                .hide(fragment)
                                .show(new MapsFragmentExtras())
                                .commitNow();
                        NavigationActivity.navigation.setSelectedItemId(R.id.navigation_nearMe);
                        NearMeHolder.maps_flipper.setDisplayedChild(1);
                        break;
                    case 2:
                        dialog.dismiss();
                        Dialog inner = new Dialog(context);
                        inner.setContentView(R.layout.home_route_steps);
                        CardView cv = inner.findViewById(R.id.close_home_ins);
                        cv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                inner.dismiss();
                            }
                        });
                        inner.setTitle(strName);
                        inner.show();
                        setRouteAdapter(route, context, inner);
                        break;
                }
            }
        });
        builderSingle.show();
    }

    //show the directions for a bus route
    static void setRouteAdapter (Route route, Context context, Dialog dialog) {
        RecyclerView recyclerView_full = dialog.findViewById(R.id.route_recycler);
        Route_RecyclerAdapter adapter = new Route_RecyclerAdapter(route.getInstructions(), route, context);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView_full.setLayoutManager(manager);
        recyclerView_full.setItemAnimator(new DefaultItemAnimator());
        recyclerView_full.setAdapter(adapter);
    }

    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
        if (byUser) {
            times_seen++;
            NavigationActivity.tinyDB.putInt("times_seen", times_seen);
        }
    }

    static void showToolTipsHome() {
        ToolTipsManager manager = HomeFragment_User.manager;
        int position = ToolTip.POSITION_BELOW;
        int align = ToolTip.ALIGN_CENTER;
        ArrayList<View> views = new ArrayList<>();
        views.add(favs);
        views.add(previous_trips);
        views.add(NavigationActivity.legendButton);
        views.add(ll_home_sheet);
        for (View view: views) {
            displayToolTip(manager, position, align, view);
        }
    }

    private static void displayToolTip(ToolTipsManager manager, int position, int align, View view) {
        String message = "";

        switch (view.getId()) {
            case R.id.previous_trips:
                message = "Your Trips Are Tracked Here.";
                break;
            case R.id.favs:
                message = "Your Favourite Places Are Kept Here.";
                break;
            case R.id.legend_show:
                //NavigationActivity.displayToolTip(position, align, view);
                break;
            case R.id.bottom_home_sheet:
                message = "Check If Everything Is Normal.";
                position = ToolTip.POSITION_ABOVE;
                break;
        }
        if (!message.equals("")) {
            ToolTip.Builder builder = new ToolTip.Builder(view.getContext(), view, main_container, message, position);
            builder.setAlign(align);
            builder.setBackgroundColor(Color.BLUE);
            manager.show(builder.build());
        }
        else {
            manager.findAndDismiss(view);
            ToolTip.Builder builder = new ToolTip.Builder(view.getContext(), view, main_container, "Remind Yourself of Icons.", position);
            builder.setAlign(align);
            builder.setBackgroundColor(Color.BLUE);
            manager.show(builder.build());
        }
    }

    private void initService_Updates () {
        // get the bottom sheet view
        ll_home_sheet = getView().findViewById(R.id.bottom_home_sheet);
        // init the bottom sheet behavior
        bottomSheetBehavior_Home_Sheet = BottomSheetBehavior.from(ll_home_sheet);

        // change the state of the bottom sheet
        bottomSheetBehavior_Home_Sheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        TextView tv_update = ll_home_sheet.findViewById(R.id.service_update);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                Connection.Response response = Jsoup.connect("https://mauritiusmetroexpress.mu/")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .execute();

                int statusCode = response.statusCode();
                if(statusCode == 200) {
                    //we are connected to a network
                    Document doc = Jsoup.connect("https://mauritiusmetroexpress.mu/").get();
                    Element link = doc.select("div[class=mtphr-dnt-tick mtphr-dnt-default-tick mtphr-dnt-clearfix]").first();

                    tv_update.setText(link.text());
                    tv_update.setTextSize(14.5f);
                    NavigationActivity.tinyDB.putString("Update", link.text());
                }
                else {
                    if (NavigationActivity.tinyDB.getAll().containsKey("Update")) {
                        tv_update.setText(NavigationActivity.tinyDB.getString("Update"));
                    }
                    else {
                        tv_update.setText("[You are not connected to the Internet]\nNo Updates At This Time.");
                    }
                    tv_update.setTextSize(14.5f);
                }
            }
            else {
                if (NavigationActivity.tinyDB.getAll().containsKey("Update")) {
                    tv_update.setText(NavigationActivity.tinyDB.getString("Update"));
                }
                else {
                    tv_update.setText("[You are not connected to the Internet]\nNo Updates At This Time.");
                }
                tv_update.setTextSize(14.5f);
            }

            // set callback for changes
            bottomSheetBehavior_Home_Sheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
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

                    switch (bottomSheetBehavior_Home_Sheet.getState()) {
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
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed due to " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}