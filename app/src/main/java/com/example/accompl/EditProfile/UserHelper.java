package com.example.accompl.EditProfile;

public class UserHelper {
    String username, name,  email, about, gender, birthdate, city, password;

    public UserHelper() {
    }

    public UserHelper(String username, String name, String email, String about, String gender, String birthdate, String city ) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.about = about;
        this.gender = gender;
        this.birthdate = birthdate;
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
