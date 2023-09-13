package com.example.loginandregister.admin;

public class LocationData {
    private static LocationData instance;
    private double adminLatitude;
    private double adminLongitude;

    // Private constructor to prevent instantiation from other classes
    private LocationData() {}

    // Public method to get the single instance of LocationData
    public static LocationData getInstance() {
        if (instance == null) {
            instance = new LocationData();
        }
        return instance;
    }
    public double getAdminLatitude() {
        return adminLatitude;
    }

    public void setAdminLatitude(double latitude) {
        this.adminLatitude = latitude;
    }

    public double getAdminLongitude() {
        return adminLongitude;
    }

    public void setAdminLongitude(double longitude) {
        this.adminLongitude = longitude;
    }
}
