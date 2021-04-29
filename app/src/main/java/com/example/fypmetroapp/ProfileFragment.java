package com.example.fypmetroapp;

import android.content.Intent;
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

import com.example.fypmetroapp.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;

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

                DocumentReference documentReference = NavigationActivity.firebaseFirestore.collection("users").document(userid);
                documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (documentSnapshot != null) {
                            //show users full name
                            fullName.setText("Hello, " + documentSnapshot.getString("full_name"));
                            pointsView.setText(String.valueOf(documentSnapshot.getLong("points")));
                            tripsView.setText(String.valueOf(documentSnapshot.getLong("trips")));
                        }
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