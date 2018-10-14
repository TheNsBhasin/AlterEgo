package com.nsbhasin.alterego.database.entity;

import android.support.annotation.NonNull;

public class User {
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";

    private String name;
    private String email;
    private String password;
    private int age;
    private String gender;
    private String tagline;
    private String image;
    private String interests;

    public User() {
        name = "";
        age = 18;
        gender = MALE;
        tagline = "";
        image = "";
        email = "";
        password = "";
        interests = "";
    }

    public User(String interests) {
        this.interests = interests;
    }

    public User(String name, int age, String gender, String tagline, String image, String interests) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.tagline = tagline;
        this.image = image;
        this.interests = interests;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + getName() +
                ", Age: " + getAge() +
                ", Gender: " + getGender() +
                ", Tagline: " + getTagline() +
                ", Interests: " + getInterests();
    }
}
