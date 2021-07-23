package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;

public class OccupancyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Get the geofences that were triggered. A single event can trigger
        // multiple geofences.
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        String locId = triggeringGeofences.get(0).getRequestId();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firebaseFirestore.collection("stationdetails");

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            for (Geofence geofence: triggeringGeofences)
            if (NavigationActivity.tinyDB.getAll().containsKey("Entered")) {
                String previous = NavigationActivity.tinyDB.getString("Entered");
                if (!previous.equals(locId)) {
                    Log.e("changed now entered", locId);
                    incrementCounter(reference, locId);
                    NavigationActivity.tinyDB.putString("Entered", locId);
                    sendNotification(locId, context);
                }
                else {
                    Log.e("entered again", locId);
                }
            }
            else {
                Log.e("first time ever", locId);
                if (incrementCounter(reference, locId) == null) {
                    createCounter(reference, locId);
                }
                NavigationActivity.tinyDB.putString("Entered", locId);
                sendNotification(locId, context);
            }

            Intent serviceIntent = new Intent(context, LocationServices.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            }else{
                context.startService(serviceIntent);
            }
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            decrementCounter(reference, locId);
            Log.e("user left", locId);
        }
    }

    @SuppressLint("NewApi")
    private void sendNotification(String locId, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.track_location)
                .setContentTitle("Location Reached")
                .setContentText("You reached " + locId)
                .setContent(null)
                .setSound(DEFAULT_NOTIFICATION_URI)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                App.CHANNEL_ID,
                "Notifs",
                NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("Description");
        channel.setLightColor(Color.BLUE);
        channel.enableLights(true);
        channel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    public Task<Void> createCounter(final CollectionReference ref, final String stationName) {
        DocumentReference doc = ref.document("occupancy");
        Counter counter = new Counter(stationName, 1);
        // Initialize the counter document, then initialize each shard.
        return doc.set(counter);
    }

    @SuppressLint("NewApi")
    public Task<Void> incrementCounter(final CollectionReference ref, String name) {
        UserUpdates.updatedOcc = false;
        DocumentReference occRef = ref.document("occupancy");
        return occRef.update(name, FieldValue.increment(1));
    }

    public Task<Integer> getCount(final DocumentReference ref, String stationName) {
        // Sum the count of each shard in the subcollection
        //DocumentReference doc = ref.collection("stationdetails").document("occupancy");
        for (Station station: MapsFragmentExtras.allStations) {
            Log.e("checked", station.name);
            if (station.name.equals(stationName)) {
                ref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            double occupancy = document.getDouble(stationName);
                            station.setOccupancy((int) occupancy);
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                });

                ref.addSnapshotListener((snapshot, error) -> {
                    double occupancy = snapshot.getDouble(stationName);
                    station.setOccupancy((int) occupancy);
                });
            }
        }
        return null;
    }

    public Task<Void> decrementCounter (final CollectionReference ref, String name) {
        DocumentReference occRef = ref.document("occupancy");
        getCount(occRef, name);
        return occRef.update(name, FieldValue.increment(-1));
    }
}