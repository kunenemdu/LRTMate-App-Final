package com.example.fypmetroapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UserPreferencesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button savePrefs;
    SharedPreferences sharedPreferences;
    String uid;
    Map<String, Object> this_user;
    DocumentReference documentPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this_user = new HashMap<>();
        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        //Log.e("user", userid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            Spinner freq_spinner = (Spinner) getView().findViewById(R.id.frequency_spinner);
            Spinner life_spinner = (Spinner) getView().findViewById(R.id.lifestyle_spinner);
            Spinner preferred_spinner = (Spinner) getView().findViewById(R.id.preferred_spinner);
            Spinner tolerance = (Spinner) getView().findViewById(R.id.tolerance_spinner);
            Spinner disabled = (Spinner) getView().findViewById(R.id.disabled_spinner);
            savePrefs = getView().findViewById(R.id.savePrefs);

        // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> freq_adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.frequency, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> life_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.lifestyle, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> tolerance_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.delay_tolerance, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> disabled_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.disabled, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> preferred_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.preferred_vehicle, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        freq_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        life_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tolerance_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disabled_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferred_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        freq_spinner.setAdapter(freq_adapter);
        life_spinner.setAdapter(life_adapter);
        tolerance.setAdapter(tolerance_adapter);
        disabled.setAdapter(disabled_adapter);
        preferred_spinner.setAdapter(preferred_adapter);

        freq_spinner.setOnItemSelectedListener(this);
        life_spinner.setOnItemSelectedListener(this);
        tolerance.setOnItemSelectedListener(this);
        disabled.setOnItemSelectedListener(this);
        preferred_spinner.setOnItemSelectedListener(this);

        getUser();
        savePrefs.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("clicked", "save");
            DocumentReference documentReference = NavigationActivity.firebaseFirestore.collection("users")
                    .document(uid).collection("preferences").document("prefs");

            documentReference.set(this_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Preferences Saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), NavigationActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("failed", "prefs");
                    Log.e("failed", "onfailure triggered!" + e.toString());
                }
            });
        }
    };

    public void getUser () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                uid = NavigationActivity.firebaseAuth.getCurrentUser().getUid();
                SharedPreferences.Editor editor = sharedPreferences.edit();
            }
        }, 3000);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (adapterView != null){
            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
        }

        //show users full name
        switch (String.valueOf(adapterView.getResources().getResourceEntryName(adapterView.getId()))) {
            case "disabled_spinner":
                this_user.put("disabled", adapterView.getSelectedItem());
                break;
            case "tolerance_spinner":
                this_user.put("delay_tolerance", adapterView.getSelectedItem());
                break;
            case "lifestyle_spinner":
                this_user.put("lifestyle", adapterView.getSelectedItem());
                break;
            case "frequency_spinner":
                this_user.put("frequency", adapterView.getSelectedItem());
                break;
            case "preferred_spinner":
                this_user.put("preferred", adapterView.getSelectedItem());
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}