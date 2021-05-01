package com.example.fypmetroapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    ImageButton login, forgot, forgotCircle;
    EditText email, password;
    static int COUNTER = 0;
    static int ATTEMPTS = 3;
    CardView logCard;
    CardView forgotCard;
    LinearLayout forgotLinear;
    LinearLayout phoneLinear;
    LinearLayout passwordLinear;
    LinearLayout logLinear;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        progressDialog = new ProgressDialog(LoginFragment.this.getContext());
        firebaseAuth = FirebaseAuth.getInstance();
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
        if (logLinear.getVisibility() == View.GONE || passwordLinear.getVisibility() == View.GONE || passwordLinear.getVisibility() == View.GONE) {
            logLinear.setVisibility(View.VISIBLE);
            phoneLinear.setVisibility(View.VISIBLE);
            passwordLinear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login = getView().findViewById(R.id.doLogin);
        email = getView().findViewById(R.id.txtEmail);
        password = getView().findViewById(R.id.txtPassword);
        logCard = getView().findViewById(R.id.logINCard);
        forgotCard = getView().findViewById(R.id.forgotCard);
        forgotLinear = getView().findViewById(R.id.forgotLinear);
        logLinear = getView().findViewById(R.id.logLinear);
        passwordLinear = getView().findViewById(R.id.phonetext);
        phoneLinear = getView().findViewById(R.id.passwordtext);
        forgot = getView().findViewById(R.id.forgotReset);
        forgotCircle = getView().findViewById(R.id.forgot);

        login.setOnClickListener(v -> {
            String userMail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(userMail)) {
                email.setError("Email is required!");
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                password.setError("Password is required!");
                return;
            }

            if (userMail != null && userPassword != null) {
                progressDialog.setMessage("Logging in. Please wait...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(userMail, userPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Log.e("user", userid);
                        loginProgress();
                    } else {
                        Toast.makeText(getContext(), "Error Logging in! Verify your details.\nAttempts remaining: " + ATTEMPTS, Toast.LENGTH_LONG).show();
                        ATTEMPTS--;
                        progressDialog.dismiss();
                    }
                });
            }
        });

        forgotCircle.setOnClickListener(ForgotButtons);
        forgot.setOnClickListener(ForgotButtons);
    }



    private View.OnClickListener ForgotButtons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            forgotCard.setVisibility(View.GONE);
            logCard.setVisibility(View.GONE);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mainLogin, new ForgotPasswordFragment());
            fragmentTransaction
                    .commit();
        }
    };

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