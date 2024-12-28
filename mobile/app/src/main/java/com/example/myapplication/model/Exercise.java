package com.example.myapplication.model;

import java.io.Serializable;

public class Exercise implements Serializable {
    private Long id;
    private String name;
    private String type;
    private boolean completed;
    private Integer minStressLevel;
    private Integer maxStressLevel;

    private String imageUrl;
    private Integer duration; // en minutes
    private Integer calories;
    private String difficulty;
    private String description;
    private String instructions;
    private String benefits;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String videoUrl;

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

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public Integer getMinStressLevel() {
        return minStressLevel;
    }

    public void setMinStressLevel(Integer minStressLevel) {
        this.minStressLevel = minStressLevel;
    }

    public Integer getMaxStressLevel() {
        return maxStressLevel;
    }

    public void setMaxStressLevel(Integer maxStressLevel) {
        this.maxStressLevel = maxStressLevel;
    }
}