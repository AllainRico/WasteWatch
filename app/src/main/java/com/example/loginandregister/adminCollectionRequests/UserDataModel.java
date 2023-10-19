package com.example.loginandregister.adminCollectionRequests;

public class UserDataModel {
    private String username;
    private double lat;
    private double lon;

    public UserDataModel(String username, double lat, double lon) {
        this.username = username;
        this.lat = lat;
        this.lon = lon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
