package com.example.myapplication.model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String email;
    private String password;

    // Liste des niveaux de stress avec leurs dates
    private List<StressEntry> stressEntries = new ArrayList<>();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User(Long id, String firstName, String lastName, String phoneNumber, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    // Getter et Setter pour la liste des niveaux de stress
    public List<StressEntry> getStressEntries() {
        return stressEntries;
    }

    public void setStressEntries(List<StressEntry> stressEntries) {
        this.stressEntries = stressEntries;
    }
    public JsonObject toUpdateJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("phoneNumber", phoneNumber);
        jsonObject.addProperty("address", address);
        return jsonObject;
    }
}
