package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ExerciseAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Historique;
import com.google.android.material.button.MaterialButton;

import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.IValue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationsFragment extends Fragment implements ExerciseAdapter.OnExerciseToggleListener {

    private static final String TAG = "RecommendationsFragment";

    private RecyclerView exerciseRecyclerView;
    private ProgressBar stressLevel;
    private TextView resultText;
    private EditText inputText;
    private MaterialButton analyzeButton;

    private ApiService apiService;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> recommendedExercises = new ArrayList<>();
    private List<Historique> userHistory = new ArrayList<>();
    private Long userId;

    private StressAnalyzer stressAnalyzer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendations, container, false);

        initializeViews(view);
        initializeStressAnalyzer();
        getUserId();
        setupRecyclerView();

        return view;
    }

    private void initializeViews(View view) {
        stressLevel = view.findViewById(R.id.stressLevel);
        resultText = view.findViewById(R.id.resultText);
        exerciseRecyclerView = view.findViewById(R.id.exerciseRecyclerView);
        inputText = view.findViewById(R.id.inputText);
        analyzeButton = view.findViewById(R.id.analyzeButton);

        apiService = ApiClient.getClient().create(ApiService.class);

        analyzeButton.setOnClickListener(v -> {
            String text = inputText.getText().toString();
            if (!text.isEmpty()) {
                analyzeStressFromInput(text);
            } else {
                inputText.setError("Veuillez entrer un message");
            }
        });
    }

    private void initializeStressAnalyzer() {
        new Thread(() -> {
            try {
                stressAnalyzer = new StressAnalyzer(requireContext());
                requireActivity().runOnUiThread(() -> analyzeButton.setEnabled(true));
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de l'initialisation du StressAnalyzer", e);
                requireActivity().runOnUiThread(() -> {
                    resultText.setText("Erreur lors de l'initialisation de StressAnalyzer.");
                    analyzeButton.setEnabled(false);
                });
            }
        }).start();
    }

    private void getUserId() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getLong("USER_ID", -1);
        Log.d(TAG, "ID utilisateur récupéré : " + userId);
    }

    private void setupRecyclerView() {
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseAdapter = new ExerciseAdapter(requireContext(), recommendedExercises, this);
        exerciseRecyclerView.setAdapter(exerciseAdapter);
    }

    private void analyzeStressFromInput(String input) {
        new Thread(() -> {
            try {
                float stressScore = stressAnalyzer.analyzeStress(input); // Utilisation de StressAnalyzer
                int stressPercentage = Math.min(100, (int) (stressScore * 100));

                requireActivity().runOnUiThread(() -> {
                    updateUI(stressPercentage);
                    saveStressLevelToDatabase(stressPercentage);
                    fetchRecommendedExercises(stressPercentage);
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de l'analyse du stress", e);
                requireActivity().runOnUiThread(() -> resultText.setText("Erreur lors de l'analyse du stress."));
            }
        }).start();
    }

    private void updateUI(int stressScore) {
        stressLevel.setProgress(stressScore);
        String message = String.format("Stress Level : %d%%", stressScore);
        String recommendation = getRecommendationText(stressScore);
        resultText.setText(message + recommendation);
    }

    private String getRecommendationText(int stressScore) {
        if (stressScore >= 75) {
            return "\n\nHigh stress level. Here are some recommended exercises to help you relax:";
        } else if (stressScore >= 50) {
            return "\n\nModerate stress level. These exercises can help you manage your stress:";
        } else if (stressScore >= 25) {
            return "\n\nLow stress level. Here are some preventive exercises:";
        } else {
            return "\n\nVery low stress level. These exercises will help you maintain this state:";
        }
    }


    private void saveStressLevelToDatabase(int stressScore) {
        if (userId == -1) {
            Log.e(TAG, "Utilisateur non connecté. Impossible d'enregistrer le niveau de stress.");
            return;
        }

        Call<Void> call = apiService.addStressLevel(userId, stressScore);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Niveau de stress enregistré avec succès.");
                } else {
                    Log.e(TAG, "Erreur lors de l'enregistrement du niveau de stress : " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau lors de l'enregistrement du niveau de stress.", t);
            }
        });
    }

    private void fetchRecommendedExercises(int stressScore) {
        Call<List<Exercise>> call = apiService.getExercisesWithinStressRange(stressScore);
        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(@NonNull Call<List<Exercise>> call, @NonNull Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedExercises.clear();
                    recommendedExercises.addAll(response.body());
                    loadUserHistory();
                } else {
                    Log.e(TAG, "Erreur réponse API exercices : " + response.code());
                    resultText.setText("Erreur lors du chargement des recommandations.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Exercise>> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau pour les exercices : ", t);
                resultText.setText("Erreur de connexion au serveur.");
            }
        });
    }

    private void loadUserHistory() {
        apiService.getHistoriqueForUser(userId).enqueue(new Callback<List<Historique>>() {
            @Override
            public void onResponse(@NonNull Call<List<Historique>> call, @NonNull Response<List<Historique>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userHistory = response.body();
                    synchronizeExercisesWithHistory();
                } else {
                    Log.e(TAG, "Erreur réponse API historique : " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Historique>> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau pour l'historique : ", t);
                Toast.makeText(requireContext(), "Erreur lors du chargement de l'historique.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void synchronizeExercisesWithHistory() {
        for (Exercise exercise : recommendedExercises) {
            exercise.setCompleted(false);
            for (Historique hist : userHistory) {
                if (exercise.getId().equals(hist.getExerciseId())) {
                    exercise.setCompleted(true);
                    break;
                }
            }
        }
        exerciseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onToggle(Exercise exercise) {
        boolean newState = !exercise.isCompleted();
        Call<Void> call = newState
                ? apiService.ajouterHistorique(userId, exercise.getId())
                : apiService.supprimerHistorique(userId, exercise.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    exercise.setCompleted(newState);
                    exerciseAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Statut mis à jour avec succès.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Erreur de mise à jour.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Erreur réseau lors de la mise à jour.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
