package com.example.geolocation.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surve on 21-Jan-18.
 */

public class Users extends RealmObject{

    @PrimaryKey
    private String number;

    private String name;
    private double latitude;
    private double longitude;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lattitude) {
        this.latitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
