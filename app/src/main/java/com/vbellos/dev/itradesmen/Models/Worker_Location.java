package com.vbellos.dev.itradesmen.Models;

public class Worker_Location {
    String worker_id;
    double lat,lng;
    long Timestamp;

    public Worker_Location() {
    }

    public Worker_Location(String worker_id, double lat, double lng, long timestamp) {
        this.worker_id = worker_id;
        this.lat = lat;
        this.lng = lng;
        Timestamp = timestamp;
    }

    public String getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(String worker_id) {
        this.worker_id = worker_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }
}
