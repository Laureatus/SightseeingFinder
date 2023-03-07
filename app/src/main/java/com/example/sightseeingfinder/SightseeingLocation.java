package com.example.sightseeingfinder;

public class SightseeingLocation {
    private double longitude;
    private double latitude;

    private String name;

    public SightseeingLocation(double longitude, double latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

}
