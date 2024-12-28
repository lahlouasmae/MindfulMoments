package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String imageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Historique> historiques = new ArrayList<>();

    @ElementCollection
    private List<Integer> stressValues = new ArrayList<>(); // Liste des niveaux de stress


    // Getters et Setters pour les nouveaux champs
    public List<Integer> getStressValues() {
        return stressValues;
    }

    public void setStressValues(List<Integer> stressValues) {
        this.stressValues = stressValues;
    }





    // Méthode pour ajouter un niveau de stress
    public void addStressLevel(int stressValue) {
        if (stressValues.size() >= 7) {
            stressValues.remove(0); // Supprimer l'ancien niveau de stress

        }
        stressValues.add(stressValue);
    }

    // Méthode pour calculer la moyenne des niveaux de stress
    public double calculateAverageStressLevel() {
        return stressValues.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // Getters et Setters existants
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Historique> getHistoriques() {
        return historiques;
    }

    public void setHistoriques(List<Historique> historiques) {
        this.historiques = historiques;
    }
}
