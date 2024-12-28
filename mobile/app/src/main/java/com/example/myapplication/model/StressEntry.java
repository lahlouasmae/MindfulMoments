package com.example.myapplication.model;

import java.time.LocalDate;

public class StressEntry {
    private int stressValue;
    private LocalDate date;

    public StressEntry() {}

    public StressEntry(int stressValue, LocalDate date) {
        this.stressValue = stressValue;
        this.date = date;
    }

    public int getStressValue() {
        return stressValue;
    }

    public void setStressValue(int stressValue) {
        this.stressValue = stressValue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
