package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    private ImageView exerciseImage;
    private TextView exerciseName, exerciseType;
    private TextView durationValueText, caloriesValueText, difficultyValueText;
    private TextView exerciseDescription, exerciseInstructions, exerciseBenefits;
    private MaterialButton completeButton;
    private Exercise currentExercise;
    private ApiService apiService;
    private Long exerciseId;
    private boolean exerciseState;
    private VideoView videoView; // Déclaration du VideoView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        apiService = ApiClient.getClient().create(ApiService.class);
        initializeViews();
        getIntentData();
    }

    private void initializeViews() {
        exerciseImage = findViewById(R.id.exerciseImage);
        exerciseName = findViewById(R.id.exerciseName);
        exerciseType = findViewById(R.id.exerciseType);
        durationValueText = findViewById(R.id.durationValue);
        caloriesValueText = findViewById(R.id.caloriesValue);
        difficultyValueText = findViewById(R.id.difficultyValue);
        exerciseDescription = findViewById(R.id.exerciseDescription);
        exerciseInstructions = findViewById(R.id.exerciseInstructions);
        exerciseBenefits = findViewById(R.id.exerciseBenefits);
        completeButton = findViewById(R.id.completeButton);
        videoView = findViewById(R.id.videoView); // Initialisation du VideoView

        completeButton.setOnClickListener(v -> toggleExerciseCompletion());
    }

    private void getIntentData() {
        exerciseId = getIntent().getLongExtra("exerciseId", -1L);

        if (exerciseId != -1L) {
            loadExerciseDetails();
        } else {
            Toast.makeText(this, "Exercice non trouvé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadExerciseDetails() {
        Call<Exercise> call = apiService.getExerciseById(exerciseId);
        call.enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentExercise = response.body();
                    updateUI();
                } else {
                    Toast.makeText(ExerciseDetailActivity.this, "Erreur lors du chargement des détails", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Toast.makeText(ExerciseDetailActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentExercise != null) {
            // Charger l'image de l'exercice
            loadImageFromUrl(currentExercise.getImageUrl());

            // Mettre à jour les autres champs de l'exercice
            exerciseName.setText(currentExercise.getName());
            exerciseType.setText(currentExercise.getType());
            durationValueText.setText(String.format("%d min", currentExercise.getDuration()));
            caloriesValueText.setText(String.valueOf(currentExercise.getCalories()));
            difficultyValueText.setText(currentExercise.getDifficulty());
            exerciseDescription.setText(currentExercise.getDescription());
            exerciseInstructions.setText(currentExercise.getInstructions());
            exerciseBenefits.setText(currentExercise.getBenefits());

            // Charger la vidéo depuis l'URL
            String videoUrl = currentExercise.getVideoUrl();
            Log.d("VIDEO_URL", "URL de la vidéo : " + videoUrl);

            if (videoUrl != null && !videoUrl.isEmpty()) {
                // Construire l'URL complète
                String fullVideoUrl = "http://10.0.2.2:8080" + videoUrl; // Remplacez avec votre serveur réel

                // Charger la vidéo dans le VideoView
                videoView.setVideoURI(Uri.parse(fullVideoUrl));
                videoView.start(); // Démarrer la lecture
            } else {
                Log.e("VIDEO_URL_ERROR", "L'URL de la vidéo est nulle ou vide.");
                Toast.makeText(ExerciseDetailActivity.this, "L'URL de la vidéo est nulle ou vide", Toast.LENGTH_SHORT).show();
            }

            // Mettre à jour l'état de complétion
            exerciseState = currentExercise.isCompleted();
            updateCompletionStatus();
        } else {
            Toast.makeText(this, "Exercice introuvable", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void loadImageFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e("IMAGE_LOADING", "L'URL de l'image est nulle ou vide");
            exerciseImage.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        // Nettoyer l'URL pour éviter les doublons
        String fullImageUrl;
        if (imageUrl.startsWith("http")) {
            fullImageUrl = imageUrl; // Si l'URL est complète, on l'utilise directement
        } else {
            fullImageUrl = "http://10.0.2.2:8080/api/exercises/images/" + imageUrl.replaceFirst("^/+", "").replaceFirst("^uploads/images/", "");
        }

        Log.d("IMAGE_LOADING", "URL complète de l'image : " + fullImageUrl);

        Glide.with(this)
                .load(fullImageUrl + "?timestamp=" + System.currentTimeMillis()) // Ajout d'un timestamp pour éviter le cache
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("IMAGE_LOADING", "Échec du chargement de l'image", e);
                        return false; // Permet d'afficher l'image d'erreur par défaut
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d("IMAGE_LOADING", "Image chargée avec succès");
                        return false;
                    }
                })
                .into(exerciseImage);
    }




    private void toggleExerciseCompletion() {
        if (currentExercise != null) {
            Call<Exercise> call = apiService.toggleExerciseCompletion(exerciseId);
            call.enqueue(new Callback<Exercise>() {
                @Override
                public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentExercise = response.body();
                        exerciseState = currentExercise.isCompleted();
                        updateCompletionStatus();
                        Toast.makeText(ExerciseDetailActivity.this,
                                "État mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ExerciseDetailActivity.this,
                                "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Exercise> call, Throwable t) {
                    Toast.makeText(ExerciseDetailActivity.this,
                            "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateCompletionStatus() {
        completeButton.setText(exerciseState ? "exercice complété" : "exercice non complété");
    }
}
