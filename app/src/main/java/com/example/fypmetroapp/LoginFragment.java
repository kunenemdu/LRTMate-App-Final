package com.example.fypmetroapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    ImageButton login, reset;
    LinearLayout emailLL, passLL, resetLL, roleLL, logDetailsLL, logButtonLL;
    EditText email, password;
    static int COUNTER = 0;
    static int ATTEMPTS = 3;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    SharedPreferences prefs;
    FirebaseFirestore firebaseFirestore;
    Map<String, Object> this_user;
    Spinner role_spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        progressDialog = new ProgressDialog(LoginFragment.this.getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        this_user = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (logDetailsLL.getVisibility() == View.GONE || logButtonLL.getVisibility() == View.GONE || resetLL.getVisibility() == View.GONE) {
            logDetailsLL.setVisibility(View.VISIBLE);
            logButtonLL.setVisibility(View.VISIBLE);
            resetLL.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login = getView().findViewById(R.id.doLogin);
        reset = getView().findViewById(R.id.resetBtn);
        email = getView().findViewById(R.id.txtEmail);
        password = getView().findViewById(R.id.txtPassword);

        logDetailsLL = getView().findViewById(R.id.logDetailsLL);
        passLL = getView().findViewById(R.id.passwordLL);
        resetLL = getView().findViewById(R.id.resetLL);
        roleLL = getView().findViewById(R.id.roleLL);
        logButtonLL = getView().findViewById(R.id.logButtonLL);
        emailLL = getView().findViewById(R.id.emailLL);

        role_spinner = (Spinner) getView().findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> role_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.roles, android.R.layout.simple_spinner_item);
        role_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(role_adapter);
        role_spinner.setOnItemSelectedListener(this);

        resetLL.setOnClickListener(ResetClickListener);
        reset.setOnClickListener(ResetClickListener);
        login.setOnClickListener(LoginClickListener);
    }

    private final View.OnClickListener ResetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logDetailsLL.setVisibility(View.GONE);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mainLogin, new ForgotPasswordFragment());
            fragmentTransaction
                    .commit();
        }
    };

    private final View.OnClickListener LoginClickListener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {

            //hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            String userMail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String role = role_spinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(userMail)) {
                email.setError("Email is required!");
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                password.setError("Password is required!");
                return;
            }

            if (!userMail.isEmpty() && !userPassword.isEmpty()) {
                progressDialog.setMessage("Logging in. Please wait...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(userMail, userPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        //Log.e("user", userid);

                        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("UserPrefs", Config.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("role", role);
                        editor.putString("user_id", uid);

                        DocumentReference documentReference = firebaseFirestore
                                .collection(role).document(uid);
                        documentReference.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();
                                String name = documentSnapshot.getString("full_name");
                                editor.putString("full_name", name);
                                Log.e("user is", documentSnapshot.toString());
                                loginProgress();
                            } else {
                                Intent loginFailed = new Intent(LoginFragment.this.getContext(), LoginFragment.class);
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(loginFailed);
                            }
                        });

                        editor.apply();
                        Log.e("prefs are", pref.getAll().toString());

                    } else {
                        ATTEMPTS--;
                        while (ATTEMPTS > 0) {
                            Toast.makeText(getContext(), "Error Logging in! Verify your details.\nAttempts remaining: " + ATTEMPTS, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                        if (ATTEMPTS <= 0) {
                            Toast.makeText(getContext(), "Please Reset Your Password.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            logDetailsLL.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    };

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

    public void loginProgress () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(LoginFragment.this.getContext(), NavigationActivity.class);
                progressDialog.dismiss();
                startActivity(loginIntent);
            }
        }, 1500);
    }
}