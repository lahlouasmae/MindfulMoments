package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Historique;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private final ApiService apiService;
    private final MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Object>> stressData = new MutableLiveData<>();
    private final MutableLiveData<Float> stressAverage = new MutableLiveData<>();

    public HistoryRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public LiveData<List<Object>> getStressData() {
        return stressData;
    }

    public LiveData<Float> getStressAverage() {
        return stressAverage;
    }

    public void loadHistorique(Long userId) {
        if (userId == null || userId == -1) return;

        apiService.getHistoriqueForUser(userId).enqueue(new Callback<List<Historique>>() {
            @Override
            public void onResponse(@NonNull Call<List<Historique>> call, @NonNull Response<List<Historique>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> completedExercises = new ArrayList<>();
                    for (Historique hist : response.body()) {
                        Exercise exercise = new Exercise();
                        exercise.setId(hist.getExerciseId());
                        exercise.setName(hist.getName());
                        exercise.setType(hist.getType());
                        exercise.setCompleted(true);
                        completedExercises.add(exercise);
                    }
                    exercises.setValue(completedExercises);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Historique>> call, @NonNull Throwable t) {
                // Log error or notify observers of failure
            }
        });
    }

    public void fetchStressData(Long userId) {
        if (userId == null || userId == -1) return;

        apiService.getStressData(userId).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Object> data = response.body();
                    if (!data.isEmpty()) {
                        stressData.setValue(data);
                        calculateStressAverage(data);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                // Log error or notify observers of failure
            }
        });
    }

    public void removeExercise(Long userId, Exercise exercise) {
        if (userId == null || userId == -1) return;

        apiService.supprimerHistorique(userId, exercise.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Exercise> currentExercises = exercises.getValue();
                    if (currentExercises != null) {
                        currentExercises.remove(exercise);
                        exercises.setValue(currentExercises);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Log error or notify observers of failure
            }
        });
    }

    private void calculateStressAverage(List<Object> data) {
        float sum = 0f;
        int validEntries = 0;

        for (Object item : data) {
            try {
                float stressLevel;
                if (item instanceof Number) {
                    stressLevel = ((Number) item).floatValue();
                } else {
                    stressLevel = Float.parseFloat(item.toString());
                }
                sum += stressLevel;
                validEntries++;
            } catch (NumberFormatException e) {
                // Log error
            }
        }

        if (validEntries > 0) {
            stressAverage.setValue(sum / validEntries);
        }
    }

    public List<Exercise> filterExercises(String query) {
        List<Exercise> currentExercises = exercises.getValue();
        if (currentExercises == null) return new ArrayList<>();

        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : currentExercises) {
            if (exercise.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(exercise);
            }
        }
        return filteredList;
    }
}