package com.example.fypmetroapp;

import android.preference.CheckBoxPreference;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserPreferences {

    public int userid;
    public boolean disabled;
    public ArrayList<CheckBoxPreference> preferences;



    public UserPreferences() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public UserPreferences(int userid, Boolean disabled, ArrayList<CheckBoxPreference> preferences) {
        this.userid = userid;
        this.disabled = disabled;
        this.preferences = preferences;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userid", userid);
        result.put("disabled", disabled);

        return result;
    }
}