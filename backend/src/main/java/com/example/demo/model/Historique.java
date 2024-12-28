package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    @JsonIgnore // Empêche la sérialisation complète de l'objet Exercise
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Empêche la sérialisation complète de l'objet User
    private User user;

    private boolean completed;
    private String name;
    private String type;
    private int minStressLevel;
    private int maxStressLevel;
    @Transient
    private Long exerciseId;
    @Column(length = 1000)
    private String imageUrl;

    private Integer duration; // en minutes
    private Integer calories;
    private String difficulty;
    @Column(length = 1000)
    private String benefits;

    @Column(length = 1000)
    private String videoUrl;
    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String instructions;

    public Long getExerciseId() {
        return exercise != null ? exercise.getId() : null;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }


    // Champs calculés pour exposer uniquement des données nécessaires
    @Transient
    public String getExerciseName() {
        return exercise != null ? exercise.getName() : null;
    }

    @Transient
    public String getExerciseType() {
        return exercise != null ? exercise.getType() : null;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinStressLevel() {
        return minStressLevel;
    }

    public void setMinStressLevel(int minStressLevel) {
        this.minStressLevel = minStressLevel;
    }

    public int getMaxStressLevel() {
        return maxStressLevel;
    }

    public void setMaxStressLevel(int maxStressLevel) {
        this.maxStressLevel = maxStressLevel;
    }
}
