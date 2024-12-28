package com.example.myapplication.model;

import java.io.Serializable;

public class ExerciseUpdateEvent implements Serializable {
    private final Exercise exercise;

    public ExerciseUpdateEvent(Exercise exercise) {
        this.exercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }
}
