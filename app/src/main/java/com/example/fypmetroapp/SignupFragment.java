package com.example.fypmetroapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


public class SignupFragment extends Fragment {
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    ImageButton signup;
    EditText nameEDT, emailEDT, passwordEDT, confirmPasswordEDT;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getContext(), NavigationActivity.class));
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signup = getView().findViewById(R.id.doRegister);
        nameEDT = getView().findViewById(R.id.txtName);
        emailEDT = getView().findViewById(R.id.txtEmail);
        passwordEDT = getView().findViewById(R.id.txtPass);
        confirmPasswordEDT = getView().findViewById(R.id.txtConfirm);
        progressDialog = new ProgressDialog(SignupFragment.this.getContext());

        signup.setOnClickListener(v -> {
            // Showing progress dialog at user registration time.
            progressDialog.setMessage("Please Wait While Creating Your Account...");
            progressDialog.show();
            String name = nameEDT.getText().toString().trim();
            String email = emailEDT.getText().toString().trim();
            String password = passwordEDT.getText().toString().trim();
            String confirmPassword = confirmPasswordEDT.getText().toString().trim();

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
                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        progressDialog.setMessage("Registered! Logging in...");
                                        registerProgress();
                                        uid = mAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = NavigationActivity.firebaseFirestore.collection("users").document(uid);
                                        Map<String, Object> this_user = new HashMap<>();
                                        this_user.put("full_name", name);
                                        this_user.put("email", email);
                                        this_user.put("trips", 0);

                                        //free points on registration
                                        this_user.put("points", 5);

                                        documentReference.set(this_user).addOnSuccessListener(
                                                aVoid -> Toast.makeText(getContext(), "Profile Saved for" + uid, Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Log.e(TAG, "onfailure triggered!" + e.toString()));

                                        Toast.makeText(getContext(), "Registration Success!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getContext(), NavigationActivity.class));
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Log.e("failed", "some reason");
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerProgress () {
        new Handler().postDelayed(() -> progressDialog.dismiss(), 1500);
    }
}