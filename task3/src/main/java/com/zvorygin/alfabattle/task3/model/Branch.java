package com.zvorygin.alfabattle.task3.model;

public class Branch {
    private long id;
    private String title;
    private double lat;
    private double lon;
    private String address;
    private Long distance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public double getDistanceTo(double lat, double lon) {
        double deltaLat = lat - this.lat;
        double deltaLon = lon - this.lon;

        return 2.0 * 6371 * 1000 * Math.asin(
                Math.sqrt(sqr(Math.sin(Math.toRadians(deltaLat / 2)))
                        + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(this.lat))
                        * sqr(Math.sin(Math.toRadians(deltaLon / 2)))));
    }

    private static double sqr(double d) {
        return d*d;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "address='" + address + '\'' +
                '}';
    }
}
