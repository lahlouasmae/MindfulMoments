package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcceuilFragment extends Fragment {
    private TextView tvTotalExercises, tvHistoryExercises;
    private CardView spotifyCard, btnProfile, btnAboutUs, btnRecommendations, btnExercise, btnHistory;
    private ApiService statisticsApi;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acceuil, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        initializeViews(view);
        initializeRetrofit();
        loadStatisticsFromDatabase();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        tvTotalExercises = view.findViewById(R.id.tvTotalExercises);
        tvHistoryExercises = view.findViewById(R.id.tvHistoryExercises);
        spotifyCard = view.findViewById(R.id.cardSpotify);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnAboutUs = view.findViewById(R.id.btnAboutUs);
        btnRecommendations = view.findViewById(R.id.btnRecommendations);
        btnExercise = view.findViewById(R.id.cardExercises);
        btnHistory = view.findViewById(R.id.cardHistory);
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        statisticsApi = retrofit.create(ApiService.class);
    }

    private void setupClickListeners() {
        spotifyCard.setOnClickListener(v -> openSpotify());
        btnProfile.setOnClickListener(v -> navigateToFragment(4));
        btnAboutUs.setOnClickListener(v -> navigateToFragment(5));
        btnRecommendations.setOnClickListener(v -> navigateToFragment(1));
        btnExercise.setOnClickListener(v -> navigateToFragment(2));
        btnHistory.setOnClickListener(v -> navigateToFragment(3));
    }

    private void loadStatisticsFromDatabase() {
        if (statisticsApi == null) {
            showToast("Erreur : API non initialisée.");
            return;
        }

        long userId = sharedPreferences.getLong("USER_ID", -1);

        // Appel pour obtenir le nombre total d'exercices
        statisticsApi.getExerciseCount().enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (isAdded() && getActivity() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        tvTotalExercises.setText(String.valueOf(response.body()));
                    } else {
                        showToast("Erreur lors du chargement des exercices.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                if (isAdded() && getActivity() != null) {
                    showToast("Erreur réseau.");
                }
            }
        });

        // Appel pour obtenir le nombre d'historiques
        if (userId != -1) {
            statisticsApi.getHistoriqueCount(userId).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if (isAdded() && getActivity() != null) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvHistoryExercises.setText(String.valueOf(response.body()));
                        } else {
                            showToast("Erreur lors du chargement de l'historique.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    if (isAdded() && getActivity() != null) {
                        showToast("Erreur réseau.");
                    }
                }
            });
        } else {
            tvHistoryExercises.setText("0");
        }
    }

    private void showToast(String message) {
        if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void openSpotify() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), SpotifyMusicActivity.class);
            startActivity(intent);
        }
    }

    private void navigateToFragment(int position) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).navigateToTab(position);
        }
    }
}