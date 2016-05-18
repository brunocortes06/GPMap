package com.bruno.gpmap.model;


/**
 * Created by Bruno on 07/05/2016.
 */
public class User {

    public String age;
    public String gender;
    public String name;
    public String description;
    public String hair;
    public String skin;


    public User(){

    }

    public User(String age, String gender, String name, String description, String hair, String skin) {
        this.age = age;
        this.gender = gender;
        this.name = name;
        this.description = description;
        this.hair = hair;
        this.skin = skin;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getSkin() {
        return skin;
    }

    public String getDescription() {
        return description;
    }

    public String getHair() {
        return hair;
    }
}
