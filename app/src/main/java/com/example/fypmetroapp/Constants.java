package com.example.fypmetroapp;

final class Constants {

    private Constants() {
    }

    private static final String PACKAGE_NAME = "com.example.fypmetroapp";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static final String APIKEY = "DenCQtAmEE7AmZQNxMmxDDOCqA9U3yaT";

    static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km

    public static final String DEFAULT_CHANNEL_ID = "miscellaneous";

    final static long LOCATION_TIMEOUT_MS = 5000;
}