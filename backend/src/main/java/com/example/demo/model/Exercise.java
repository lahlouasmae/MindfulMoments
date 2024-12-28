package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private boolean completed;
    private Integer minStressLevel;
    private Integer maxStressLevel;
    @Column(length = 1000)
    private String imageUrl;

    private Integer duration; // en minutes
    private Integer calories;
    private String difficulty;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String instructions;

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Column(length = 1000)
    private String benefits;
    private String videoUrl;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Évite la sérialisation récursive
    private List<Historique> historiques = new ArrayList<>();

    // Constructeur par défaut
    public Exercise() {}

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

    public List<Historique> getHistoriques() {
        return historiques;
    }

    public void setHistoriques(List<Historique> historiques) {
        this.historiques = historiques;
    }

    // Méthodes utilitaires
    public void addHistorique(Historique historique) {
        historiques.add(historique);
        historique.setExercise(this);
    }

    public void removeHistorique(Historique historique) {
        historiques.remove(historique);
        historique.setExercise(null);
    }

    // equals et hashCode basés sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise)) return false;
        Exercise exercise = (Exercise) o;
        return id != null && id.equals(exercise.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
