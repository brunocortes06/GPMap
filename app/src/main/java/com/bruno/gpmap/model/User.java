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
    public long tel;
    public String uid;


    public User(){

    }

    public User(String age, String gender, String name, String description, String hair, String skin, Long tel, String uid) {
        this.age = age;
        this.gender = gender;
        this.name = name;
        this.description = description;
        this.hair = hair;
        this.skin = skin;
        this.tel = tel;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public Long getTel() {
        return tel;
    }
}
