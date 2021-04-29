package com.example.fypmetroapp;

public class User {
    private static String fullname, groups, email;
    private String preferences;
    private String userid;
    private String phone;
    private Boolean disabled;

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getUserid() {
        return userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return fullname;
    }

    public void setName(String fullname) {
        this.fullname = fullname;
    }

    public static String getFullname() {
        return fullname;
    }

    public static void setFullname(String fullname) {
        User.fullname = fullname;
    }

    public static String getGroups() {
        return groups;
    }

    public static void setGroups(String groups) {
        User.groups = groups;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userid, String email, String fullname, String phone, String groups, String preferences) {
        this.userid = userid;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.groups = groups;
        this.preferences = preferences;
    }
}
