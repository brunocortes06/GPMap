package com.bruno.gpmap.manage;


/**
 * Created by Bruno on 07/05/2016.
 */
public class User {

    public String name;
    public String email;
    public String gender;
    public String height;
    public String password;
    public String lat;
    public String lng;

    public User(){

    }

    public User(String name, String email, String gender, String height, String password, String lat, String lng) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.height = height;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
