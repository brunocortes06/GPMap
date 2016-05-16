package com.bruno.gpmap.model;


/**
 * Created by Bruno on 07/05/2016.
 */
public class User {

    public String age;
    public String gender;
    public String name;

    public User(){

    }

    public User(String age, String gender, String name) {
        this.age = age;
        this.gender = gender;
        this.name = name;
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
}
