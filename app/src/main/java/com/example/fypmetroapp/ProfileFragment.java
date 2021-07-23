package com.example.fypmetroapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    TextView fullName, pointsView, tripsView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private TinyDB tinyDB = NavigationActivity.tinyDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullName = getView().findViewById(R.id.userFull);
        pointsView = getView().findViewById(R.id.points);
        tripsView = getView().findViewById(R.id.trips);
        getuser();
    }

    public void getuser () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userid = firebaseAuth.getCurrentUser().getUid();
                if (userid != null) {
                    if (NavigationActivity.tinyDB.getAll().containsKey("User")) {
                        User user = NavigationActivity.tinyDB.getObject("User", User.class);
                        String role = user.getRole();
                        Log.e("id", userid);

                        DocumentReference documentReference = firebaseFirestore.collection(role).document(userid);
                        documentReference.addSnapshotListener(getActivity(), (documentSnapshot, error) -> {
                            if (documentSnapshot != null) {
                                //show users full name
                                fullName.setText("Hello, " + user.getName());
                                pointsView.setText(String.valueOf(documentSnapshot.getLong("points")));
                                tripsView.setText(String.valueOf(documentSnapshot.getLong("trips")));
                            }
                        });
                    }
                }
            }
        }, 10000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}