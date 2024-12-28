package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.ExerciseAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.ExerciseUpdateEvent;
import com.example.myapplication.model.Historique;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseFragment extends Fragment implements ExerciseAdapter.OnExerciseToggleListener {

    private static final String TAG = "ExerciseFragment";
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ApiService apiService;
    private final List<Exercise> exercises = new ArrayList<>();
    private Long userId;
    private SearchView searchView;
    private ExerciseViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        // Récupérer l'ID utilisateur
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getLong("USER_ID", -1);
        Log.d(TAG, "User ID récupéré: " + userId);

        initializeViews(view);
        setupRecyclerView();
        setupObservers();
        setupSearchView();

        if (userId != -1) {
            loadExercises();
        } else {
            Toast.makeText(requireContext(), "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExerciseAdapter(requireContext(), exercises, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getExercises().observe(getViewLifecycleOwner(), newExercises -> {
            if (newExercises != null) {
                exercises.clear();
                exercises.addAll(newExercises);
                adapter.updateList(exercises);
                loadUserHistory(); // Charger l'historique après avoir reçu les exercices
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Vous pouvez ajouter un indicateur de chargement ici si nécessaire

        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.filterExercises(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.filterExercises(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            viewModel.filterExercises("");
            return false;
        });
    }

    private void loadExercises() {
        Log.d(TAG, "Chargement des exercices...");
        viewModel.fetchExercises(); // Utiliser le ViewModel pour charger les exercices
    }

    private void loadUserHistory() {
        apiService.getHistoriqueForUser(userId).enqueue(new Callback<List<Historique>>() {
            @Override
            public void onResponse(@NonNull Call<List<Historique>> call, @NonNull Response<List<Historique>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Historique> historique = response.body();
                    synchronizeExercisesWithHistory(historique);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Historique synchronisé et adapté mis à jour");
                } else {
                    Log.e(TAG, "Erreur lors du chargement de l'historique: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Historique>> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur lors du chargement de l'historique", t);
            }
        });
    }

    private void synchronizeExercisesWithHistory(List<Historique> historique) {
        Log.d(TAG, "Synchronisation des exercices avec l'historique...");
        for (Exercise exercise : exercises) {
            exercise.setCompleted(false);
            for (Historique hist : historique) {
                if (exercise.getId().equals(hist.getExerciseId())) {
                    exercise.setCompleted(true);
                    Log.d(TAG, "Exercice marqué comme Terminé : " + exercise.getName());
                    break;
                }
            }
        }
    }

    @Override
    public void onToggle(Exercise exercise) {
        if (userId == -1) {
            Toast.makeText(requireContext(), "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean newState = !exercise.isCompleted();
        Call<Void> call = newState
                ? apiService.ajouterHistorique(userId, exercise.getId())
                : apiService.supprimerHistorique(userId, exercise.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    exercise.setCompleted(newState);
                    adapter.notifyItemChanged(exercises.indexOf(exercise));
                    Toast.makeText(requireContext(), "Statut mis à jour avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la mise à jour.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExerciseUpdated(ExerciseUpdateEvent event) {
        Exercise updatedExercise = event.getExercise();
        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).getId().equals(updatedExercise.getId())) {
                exercises.get(i).setCompleted(updatedExercise.isCompleted());
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }
}
