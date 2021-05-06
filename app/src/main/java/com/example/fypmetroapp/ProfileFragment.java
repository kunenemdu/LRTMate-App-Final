package com.example.fypmetroapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    TextView fullName, pointsView, tripsView;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
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
                //Log.e("user", userid);
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("UserPrefs", Config.MODE_PRIVATE);
                String role = pref.getString("role", null);

                DocumentReference documentReference = NavigationActivity.firebaseFirestore.collection(role).document(userid);
                documentReference.addSnapshotListener(getActivity(), (documentSnapshot, error) -> {
                    if (documentSnapshot != null) {
                        //show users full name
                        fullName.setText("Hello, " + documentSnapshot.getString("full_name"));
                        pointsView.setText(String.valueOf(documentSnapshot.getLong("points")));
                        tripsView.setText(String.valueOf(documentSnapshot.getLong("trips")));
                    }
                });
            }
        }, 3000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}