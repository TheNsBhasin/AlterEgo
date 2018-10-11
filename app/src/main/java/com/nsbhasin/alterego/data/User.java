package com.nsbhasin.alterego.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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
    private List<Interest> interests;

    public User() {
        name = "";
        age = 18;
        gender = MALE;
        tagline = "";
        image = "";
        email = "";
        password = "";
        interests = new ArrayList<>();
    }

    public User(List<Interest> interests) {
        this.interests = interests;
    }

    public User(String name, int age, String gender, String tagline, String image, List<Interest> interests) {
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

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ")
                .append(getName())
                .append(", Age: ")
                .append(getAge())
                .append(", Gender: ")
                .append(getGender())
                .append(", Tagline: ")
                .append(getTagline())
                .append(", Interests: ");
        for (int i = 0; i < getInterests().size(); ++i) {
            if (getInterests().get(i).isChecked()) {
                builder.append(interests.get(i).getInterest());
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
