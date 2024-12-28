package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ExerciseViewModel.java
public class ExerciseViewModel extends ViewModel {
    private final MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final ApiService apiService;
    private List<Exercise> originalList = new ArrayList<>(); // Liste originale complète

    public ExerciseViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchExercises() {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            return;
        }

        isLoading.setValue(true);
        apiService.getAllExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(@NonNull Call<List<Exercise>> call,
                                   @NonNull Response<List<Exercise>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    originalList = new ArrayList<>(response.body()); // Sauvegarde de la liste originale
                    exercises.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Exercise>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                // Gérer l'erreur si nécessaire
            }
        });
    }

    public void updateExercise(Exercise updatedExercise) {
        // Mettre à jour dans la liste originale
        boolean found = false;
        for (int i = 0; i < originalList.size(); i++) {
            if (originalList.get(i).getId().equals(updatedExercise.getId())) {
                originalList.set(i, updatedExercise);
                found = true;
                break;
            }
        }
        if (!found) {
            originalList.add(updatedExercise);
        }

        // Mettre à jour la liste affichée
        List<Exercise> currentList = exercises.getValue();
        if (currentList == null) return;

        List<Exercise> newList = new ArrayList<>(currentList);
        found = false;
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).getId().equals(updatedExercise.getId())) {
                newList.set(i, updatedExercise);
                found = true;
                break;
            }
        }
        if (!found) {
            newList.add(updatedExercise);
        }

        exercises.setValue(newList);
    }

    public void filterExercises(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Si la recherche est vide, on retourne la liste originale complète
            exercises.setValue(new ArrayList<>(originalList));
            return;
        }

        // Filtrer à partir de la liste originale
        List<Exercise> filteredList = originalList.stream()
                .filter(exercise -> exercise.getName().toLowerCase()
                        .contains(query.toLowerCase().trim()))
                .collect(Collectors.toList());

        exercises.setValue(filteredList);
    }
}

