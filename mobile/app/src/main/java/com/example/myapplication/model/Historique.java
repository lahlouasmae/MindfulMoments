package com.example.myapplication.model;

public class Historique {

    private Long id;
    private Long userId;
    private Long exerciseId; // Ajout de l'identifiant de l'exercice
    private String name;
    private String type;
    private boolean completed;
    private int minStressLevel;
    private int maxStressLevel;

    private String imageUrl;
    private Integer duration; // en minutes
    private Integer calories;
    private String difficulty;
    private String description;

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

    private String instructions;
    private String benefits;
    // Constructeurs
    public Historique() {
    }

    public Historique(Long id, Long userId, Long exerciseId, String name, String type, boolean completed, int minStressLevel, int maxStressLevel) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId; // Initialisation de exerciseId
        this.name = name;
        this.type = type;
        this.completed = completed;
        this.minStressLevel = minStressLevel;
        this.maxStressLevel = maxStressLevel;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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
