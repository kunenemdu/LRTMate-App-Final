package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static android.graphics.Color.TRANSPARENT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowRouteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Dialog buses_at_station;
    ProgressBar loaderBar;
    private TinyDB tinyDB = NavigationActivity.tinyDB;
    LinearLayout loader, main;
    private int progressStatus = 0;
    private Handler progress_handler = new Handler();
    final Handler timeHandler = new Handler(Looper.getMainLooper());
    static TickerView next_arrivalTicker, cur_stationTicker, progress_ticker;
    static TextView status, proximity, occupancy, statType, init;
    static Station cur_station, next_station;
    static Bus bus_to_track;

    public FollowRouteFragment() {
        // Required empty public constructor
    }

    public static Station getCur_station() {
        return cur_station;
    }

    public static void setCur_station(Station cur_station) {
        FollowRouteFragment.cur_station = cur_station;
    }

    @Override
    public void onStop() {
        timeHandler.removeCallbacks(new Runnable() {
            @Override
            public void run() {

            }
        });
        super.onStop();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowRouteFragment newInstance(String param1, String param2) {
        FollowRouteFragment fragment = new FollowRouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cur_stationTicker = getView().findViewById(R.id.curStation);
        next_arrivalTicker = getView().findViewById(R.id.nextArrival);
        occupancy = getView().findViewById(R.id.occupancy);
        statType = getView().findViewById(R.id.stationType);
        status = getView().findViewById(R.id.currentStatus);
        proximity = getView().findViewById(R.id.proximity);
        init = getView().findViewById(R.id.init);

        cur_stationTicker.setCharacterLists(TickerUtils.provideAlphabeticalList());
        next_arrivalTicker.setCharacterLists(TickerUtils.provideNumberList());
        cur_stationTicker.setText("Receive Updates...");
        status.setText("Waiting to Receive Updates...");
        occupancy.setText("Waiting to Receive Updates...");
        statType.setText("Waiting to");
        next_arrivalTicker.setText("Receive Updates...");
        proximity.setText("Waiting to");
        cur_stationTicker.setTextColor(Color.BLUE);
        status.setTextColor(Color.BLACK);
        occupancy.setTextColor(Color.BLACK);
        proximity.setTextColor(Color.BLACK);
    }

    private void start_following () {
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
            }
        });
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

    @SuppressLint("SetTextI18n")
    public void initTracking() {
        //Log.e("loc", userUpdates.nearest_station.name);
        UserUpdates userUpdates = new UserUpdates();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2000);
                //time = d/s [avg walking speed humans = 6kph]
                //TODO: CALCULATE THIS USING USER'S AVG SPEED IF ON FOOT/DRIVING/CYCLING
                int time = (int) (tinyDB.getDouble("distance_to_nearest_station") / 6);
                proximity.setText("~ " + time + " min(s) from");
                proximity.setTextColor(getResources().getColor(R.color.normal_green));
                cur_stationTicker.setText(getCur_station().name);
                cur_stationTicker.setTextColor(Color.BLUE);

                occupancy.setText(Maps_Full_Access.occupancy(cur_station.name));

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
                    statType.setText(cur_station.type);
                    next_arrivalTicker.setText(arrives_in + " minute(s)");
                    //Log.e("next", userUpdates.getCur_stat_next());
                    //Log.e("time", String.valueOf(current_time.format("%k:%M")));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    private void stopTracking () {
        if (tinyDB.getBoolean("tracking") == false) {
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

    private void ShowBusesDialog(Station station) {
        buses_at_station.setContentView(R.layout.choose_bus_dialog);
        TextView txtBusName = buses_at_station.findViewById(R.id.txtBusStation_home);
        txtBusName.setText(station.name);
        buses_at_station.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        buses_at_station.show();
        Extract_BUS_Data(station);
        //TableLayout buses = buses_at_station.findViewById(R.id.buses_at_station_home);
        //buses.removeAllViews();
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
                    String clickedBus = String.valueOf(tvName.getText());
                    buses_at_station.dismiss();
                    getTimes(cur_station, String.valueOf(bus_to_track.name));
                    ImageButton closeDialog = getView().findViewById(R.id.dialog_closeX);
                    ImageView occupancy_anim = getView().findViewById(R.id.occupancy_anim);
                    //animateView();
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resumed", "follow");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("started", "follow");
    }
}