package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuffXfermode;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignupFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    ImageButton signup;
    EditText nameEDT, emailEDT, passwordEDT, confirmPasswordEDT;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    Map<String, Object> this_user;
    String uid;
    FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        mAuth = FirebaseAuth.getInstance();
        this_user = new HashMap<>();

        if (mAuth.getCurrentUser() != null) {
            if (isLocationEnabled(getContext()) == true) {
                Log.e("logged in", "on too");
                startActivity(new Intent(getContext(), NavigationActivity.class));
            }
            else {
                Log.e("logged in", "not on tho");
                startActivity(new Intent(getContext(), NoAccess.class));
            }
            return;
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public boolean isLocationEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && LocationManagerCompat.isLocationEnabled(manager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signup = getView().findViewById(R.id.doRegister);
        nameEDT = getView().findViewById(R.id.txtName);
        emailEDT = getView().findViewById(R.id.txtEmail);
        passwordEDT = getView().findViewById(R.id.txtPass);
        confirmPasswordEDT = getView().findViewById(R.id.txtConfirm);
        progressDialog = new ProgressDialog(SignupFragment.this.getContext());

        Spinner role_spinner = (Spinner) getView().findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> role_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.roles, android.R.layout.simple_spinner_item);
        role_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(role_adapter);
        role_spinner.setOnItemSelectedListener(this);

        signup.setOnClickListener(v -> {
            //hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            // Showing progress dialog at user registration time.
            progressDialog.setMessage("Please Wait While Creating Your Account...");
            progressDialog.show();
            String name = nameEDT.getText().toString().trim();
            String email = emailEDT.getText().toString().trim();
            String password = passwordEDT.getText().toString().trim();
            String confirmPassword = confirmPasswordEDT.getText().toString().trim();
            String role = role_spinner.getSelectedItem().toString();

            if (mAuth.getCurrentUser() != null) {
                progressDialog.dismiss();
                startActivity(new Intent(getContext(), NavigationActivity.class));
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailEDT.setError("Email is required!");
                progressDialog.dismiss();
                return;
            }

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                progressDialog.dismiss();
                if (TextUtils.isEmpty(password)) {
                    passwordEDT.setError("Password is required!");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    confirmPasswordEDT.setError("Password is required!");
                }
            }

            if (password.length() < 8) {
                progressDialog.dismiss();
                passwordEDT.setError("Must be minimum 8 characters!");
                return;
            }

            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
                if (password.equals(confirmPassword)) {
                    if (password.length() >= 8) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                progressDialog.setMessage("Account Created! Logging You In...");
                                progressDialog.show();
                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        uid = mAuth.getCurrentUser().getUid();
                                        this_user = new HashMap<>();

                                        this_user.put("full_name", name);
                                        this_user.put("email", email);
                                        this_user.put("trips", 0);
                                        this_user.put("role", role);
                                        if (role.equals("Driver")) {
                                            DocumentReference documentReference = firebaseFirestore
                                                    .collection(role).document(uid);
                                            //free 15 points on registration
                                            this_user.put("points", 15);
                                            documentReference.set(this_user).addOnSuccessListener(
                                                    aVoid -> Log.e("success!", "registered"+ uid))
                                                    .addOnFailureListener(e -> Log.e(TAG, "onfailure triggered!" + e.toString()));
                                        }
                                        else if (role.equals("User")) {
                                            DocumentReference documentReference = firebaseFirestore
                                                    .collection(role).document(uid);
                                            //free 5 points on registration
                                            this_user.put("points", 5);
                                            documentReference.set(this_user).addOnSuccessListener(
                                                    aVoid -> Log.e("success!", "registered"+ uid))
                                                    .addOnFailureListener(e -> Log.e(TAG, "onfailure triggered!" + e.toString()));
                                        }

                                        TinyDB tinyDB = new TinyDB(getActivity());
                                        tinyDB.putString("full_name", name);
                                        tinyDB.putString("user_id", uid);
                                        tinyDB.putString("email", email);
                                        tinyDB.putInt("trips", 0);
                                        tinyDB.putString("role", role);

                                        User user = new User(name, uid, email, role);
                                        tinyDB.putObject("User", user);

                                        if (role.equals("Driver")) {
                                            tinyDB.putInt("points", 15);
                                        }
                                        else if (role.equals("User")) {
                                            tinyDB.putInt("points", 5);
                                        }

                                        //Toast.makeText(getContext(), "Registration Success!", Toast.LENGTH_SHORT).show();
                                        registerProgress();
                                        //progressDialog.dismiss();
                                        startActivity(new Intent(getContext(), NavigationActivity.class));
                                    }
                                });
                            } else if (!task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerProgress () {
        new Handler().postDelayed(() -> progressDialog.dismiss(), 2500);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (adapterView != null){
            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
        }

        switch (String.valueOf(adapterView.getResources().getResourceEntryName(adapterView.getId()))) {
            case "role_spinner":
                this_user.put("role", adapterView.getSelectedItem());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}