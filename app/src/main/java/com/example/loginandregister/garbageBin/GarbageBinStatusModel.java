package com.example.loginandregister.garbageBin;

public class GarbageBinStatusModel {

    private int fillLevel;
    private int latitude, longitude;
    private String bin, place;


    public int getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
