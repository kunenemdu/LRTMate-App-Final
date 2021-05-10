package com.example.fypmetroapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordFragment extends Fragment {

    ImageButton recover;
    EditText phone;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recover = getView().findViewById(R.id.doRecover);
        phone = getView().findViewById(R.id.txtEmail);

        recover.setOnClickListener(v -> {
            String userPhone = phone.getText().toString().trim();

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission((AppCompatActivity) getActivity(), MY_PERMISSIONS_REQUEST_SEND_SMS,
                        Manifest.permission.SEND_SMS, true);
            }
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                CheckUser(userPhone);
            }
        });
    }

    public void hideFragments () {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.forgotPassFrag, new LoginFragment());
        fragmentTransaction
                .commit();
    }

    public void CheckUser (String phone) {
        String findExisting = "https://metromobile.000webhostapp.com/recoverPassword.php?phone="+phone;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, findExisting, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("found", "entered finding");
                try {
                    FindUser(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("found", "done finding");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        Log.e("done", "finding user");
    }

    @SuppressLint("NewApi")
    public void FindUser (String response) throws JSONException {
        Log.e("entered", "finduser started");
        JSONObject jsonObject = new JSONObject(response);
        //time object 1st
        JSONObject aTime = null;
        //put whole array into jsonArray
        JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY_LOGIN);
        for (int i = 0; i < result.length(); i++) {
            aTime = result.getJSONObject(i);
            Log.e("here", aTime.toString());

            if (aTime.getString(Config.CHECK).equals(Config.EXISTS)) {
                Log.e("entered", aTime.toString());

                //send user password with SMS
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone.getText().toString().trim(),null, "Recovered Password: "+ aTime.getString(Config.PASSWORD),null,null);
                Toast.makeText(ForgotPasswordFragment.this.getContext(), "Password Recovered! Check Your Messages!", Toast.LENGTH_LONG).show();

                hideFragments();
            }
            else if (aTime.getString(Config.CHECK).equals(Config.NEW)){
                Toast.makeText(ForgotPasswordFragment.this.getContext(), "No User Found With This Number!", Toast.LENGTH_LONG).show();
            }
        }
    }
}