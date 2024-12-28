package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.example.myapplication.adapter.AdminExerciseAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.ExerciseDetailsBottomSheet;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminExerciseFragment extends Fragment implements SwipeActionCallback {

    private static final String TAG = "AdminExerciseFragment";
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private AdminExerciseAdapter adapter;
    private ApiService apiService;
    private List<Exercise> exerciseList = new ArrayList<>();
    private int lastEditPosition = -1;
    private final ActivityResultLauncher<Intent> addExerciseLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    boolean openExercises = result.getData().getBooleanExtra("open_exercises", false);
                    if (openExercises) {
                        loadExercises(); // Recharge les exercices depuis le serveur
                        Toast.makeText(requireContext(), "Exercice ajouté avec succès", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    private final ActivityResultLauncher<Intent> editExerciseLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    loadExercises();
                } else {
                    // Si l'édition est annulée, on rafraîchit simplement l'élément à sa position
                    adapter.resetPosition(lastEditPosition);
                }
            });
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_exercise, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        addButton = view.findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new AdminExerciseAdapter(
                requireContext(),
                exerciseList,
                this::onExerciseClick,
                new AdminExerciseAdapter.OnExerciseActionListener() {
                    @Override
                    public void onEdit(Exercise exercise) {
                        startEditActivity(exercise);
                    }

                    @Override
                    public void onDelete(Exercise exercise) {
                        showDeleteConfirmation(exercise);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddExerciseActivity.class);
            addExerciseLauncher.launch(intent);
        });

        loadExercises();
        return view;
    }

    private void loadExercises() {
        apiService.getAllExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(@NonNull Call<List<Exercise>> call, @NonNull Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    exerciseList.clear();
                    exerciseList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Erreur de chargement des exercices", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Exercise>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExerciseToList(Exercise exercise) {
        if (exercise != null) {
            exerciseList.add(exercise);
            adapter.notifyItemInserted(exerciseList.size() - 1);
        }
    }

    public void updateExerciseInList(Exercise updatedExercise) {
        if (updatedExercise != null) {
            adapter.updateExercise(updatedExercise);
        }
    }

    private void onExerciseClick(Exercise exercise) {
        ExerciseDetailsBottomSheet bottomSheet = new ExerciseDetailsBottomSheet(exercise);
        bottomSheet.show(getChildFragmentManager(), "exercise_details");
    }

    private void startEditActivity(Exercise exercise) {
        lastEditPosition = exerciseList.indexOf(exercise);  // Sauvegarde la position
        Intent intent = new Intent(requireContext(), EditExerciseActivity.class);
        intent.putExtra("exerciseId", exercise.getId());
        editExerciseLauncher.launch(intent);
    }

    private void showDeleteConfirmation(Exercise exercise) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation de suppression")
                .setMessage("Voulez-vous vraiment supprimer cet exercice ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteExercise(exercise))
                .setNegativeButton("Non", (dialog, which) -> adapter.notifyDataSetChanged())
                .show();
    }

    private void deleteExercise(Exercise exercise) {
        apiService.deleteExercise(exercise.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    int position = exerciseList.indexOf(exercise);
                    exerciseList.remove(exercise);
                    adapter.notifyItemRemoved(position);
                    showUndoSnackbar(exercise, position);
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showUndoSnackbar(Exercise exercise, int position) {
        Snackbar.make(requireView(), "Exercice supprimé", Snackbar.LENGTH_LONG)
                .setAction("Annuler", v -> {
                    exerciseList.add(position, exercise);
                    adapter.notifyItemInserted(position);
                })
                .show();
    }

    @Override
    public void onDeleteSwipe(int position) {
        Exercise exercise = adapter.getExerciseAt(position);
        showDeleteConfirmation(exercise);
    }

    @Override
    public void onEditSwipe(int position) {
        Exercise exercise = adapter.getExerciseAt(position);
        startEditActivity(exercise);
    }



}