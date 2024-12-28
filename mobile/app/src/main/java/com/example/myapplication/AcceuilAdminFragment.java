package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceuilAdminFragment extends Fragment {

    private TextView tvTotalUsers, tvTotalExercises;
    private CardView btnProfile, btnExercise;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acceuil_admin, container, false);

        // Initialisation des vues (ID corrigés)
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers); // ID correct dans le XML
        tvTotalExercises = view.findViewById(R.id.tvTotalExercises); // ID correct dans le XML
        btnProfile = view.findViewById(R.id.btnProfile); // ID correct dans le XML
        btnExercise = view.findViewById(R.id.btnExercise); // ID correct dans le XML

        // Initialisation Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // Charger les statistiques
        loadStatistics();

        // Gestion des clics sur les boutons
        btnProfile.setOnClickListener(v -> navigateToTab(1)); // Gérer les utilisateurs
        btnExercise.setOnClickListener(v -> navigateToTab(2)); // Gérer les exercices

        return view;
    }

    private void loadStatistics() {
        // Charger le nombre total d'utilisateurs
        apiService.getTotalUsers().enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long totalUsers = response.body();

                    // Soustraire 1 pour exclure l'administrateur (ou un autre utilisateur spécifique)
                    if (totalUsers > 0) {
                        totalUsers -= 1; // Supposons qu'il y a au moins un administrateur
                    }

                    tvTotalUsers.setText(String.valueOf(totalUsers));
                } else {
                    Toast.makeText(getContext(), "Erreur lors du chargement des utilisateurs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur réseau lors du chargement des utilisateurs", Toast.LENGTH_SHORT).show();
            }
        });

        // Charger le nombre total d'exercices
        apiService.getExerciseCount().enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvTotalExercises.setText(String.valueOf(response.body()));
                } else {
                    Toast.makeText(getContext(), "Erreur lors du chargement des exercices", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur réseau lors du chargement des exercices", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToTab(int position) {
        // Naviguer vers l'onglet spécifié dans AdminHomeActivity
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).navigateToTab(position);
        }
    }
}