package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.api.ApiClient;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditExerciseActivity extends AppCompatActivity {

    private static final String TAG = "EditExerciseActivity";

    private EditText nameEditText, typeEditText, minStressLevelEditText, maxStressLevelEditText;
    private EditText durationEditText, caloriesEditText, difficultyEditText;
    private EditText descriptionEditText, instructionsEditText, benefitsEditText;
    private ImageView exerciseImageView;
    private VideoView exerciseVideoView;
    private Button selectImageButton, selectVideoButton, saveButton;
    private ApiService apiService;
    private Long exerciseId;

    private Uri selectedImageUri;
    private Uri selectedVideoUri;
    private String originalImageUrl;
    private String originalVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        initializeViews();

        apiService = ApiClient.getClient().create(ApiService.class);
        exerciseId = getIntent().getLongExtra("exerciseId", -1);

        if (exerciseId == -1) {
            Toast.makeText(this, "Erreur : exercice introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadExerciseDetails();

        selectImageButton.setOnClickListener(v -> openImageSelector());
        selectVideoButton.setOnClickListener(v -> openVideoSelector());
        saveButton.setOnClickListener(v -> saveExercise());
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        typeEditText = findViewById(R.id.typeEditText);
        minStressLevelEditText = findViewById(R.id.minStressLevelEditText);
        maxStressLevelEditText = findViewById(R.id.maxStressLevelEditText);
        durationEditText = findViewById(R.id.durationEditText);
        caloriesEditText = findViewById(R.id.caloriesEditText);
        difficultyEditText = findViewById(R.id.difficultyEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        benefitsEditText = findViewById(R.id.benefitsEditText);
        exerciseImageView = findViewById(R.id.exerciseImageView);
        exerciseVideoView = findViewById(R.id.exerciseVideoView);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectVideoButton = findViewById(R.id.selectVideoButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadExerciseDetails() {
        apiService.getExerciseById(exerciseId).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateExerciseDetails(response.body());
                } else {
                    Toast.makeText(EditExerciseActivity.this, "Erreur de chargement des détails.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                Toast.makeText(EditExerciseActivity.this, "Erreur de connexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateExerciseDetails(Exercise exercise) {
        originalImageUrl = exercise.getImageUrl();
        originalVideoUrl = exercise.getVideoUrl();

        nameEditText.setText(exercise.getName());
        typeEditText.setText(exercise.getType());
        minStressLevelEditText.setText(String.valueOf(exercise.getMinStressLevel()));
        maxStressLevelEditText.setText(String.valueOf(exercise.getMaxStressLevel()));
        durationEditText.setText(String.valueOf(exercise.getDuration()));
        caloriesEditText.setText(String.valueOf(exercise.getCalories()));
        difficultyEditText.setText(exercise.getDifficulty());
        descriptionEditText.setText(exercise.getDescription());
        instructionsEditText.setText(exercise.getInstructions());
        benefitsEditText.setText(exercise.getBenefits());

        loadImageFromUrl(exerciseImageView, originalImageUrl);
        loadVideoFromUrl(exerciseVideoView, originalVideoUrl);
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openVideoSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(exerciseImageView);
                }
            }
    );

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedVideoUri = result.getData().getData();
                    exerciseVideoView.setVideoURI(selectedVideoUri);
                    exerciseVideoView.setVisibility(VideoView.VISIBLE);
                    exerciseVideoView.start();
                }
            }
    );

    private void saveExercise() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String minStress = minStressLevelEditText.getText().toString().trim();
        String maxStress = maxStressLevelEditText.getText().toString().trim();
        String duration = durationEditText.getText().toString().trim();
        String calories = caloriesEditText.getText().toString().trim();
        String difficulty = difficultyEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();
        String benefits = benefitsEditText.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || minStress.isEmpty() || maxStress.isEmpty() ||
                duration.isEmpty() || calories.isEmpty() || difficulty.isEmpty() ||
                description.isEmpty() || instructions.isEmpty() || benefits.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        Exercise updatedExercise = new Exercise();
        updatedExercise.setId(exerciseId);
        updatedExercise.setName(name);
        updatedExercise.setType(type);
        updatedExercise.setMinStressLevel(Integer.parseInt(minStress));
        updatedExercise.setMaxStressLevel(Integer.parseInt(maxStress));
        updatedExercise.setDuration(Integer.parseInt(duration));
        updatedExercise.setCalories(Integer.parseInt(calories));
        updatedExercise.setDifficulty(difficulty);
        updatedExercise.setDescription(description);
        updatedExercise.setInstructions(instructions);
        updatedExercise.setBenefits(benefits);

        if (selectedImageUri != null) {
            uploadImage(exerciseId, selectedImageUri, updatedExercise);
        } else {
            updatedExercise.setImageUrl(originalImageUrl);
        }

        if (selectedVideoUri != null) {
            uploadVideo(exerciseId, selectedVideoUri, updatedExercise);
        } else {
            updatedExercise.setVideoUrl(originalVideoUrl);
        }

        apiService.updateExercise(exerciseId, updatedExercise).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditExerciseActivity.this, "Exercice mis à jour avec succès.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);  // Ajout de cette ligne
                    finish();
                } else {
                    Toast.makeText(EditExerciseActivity.this, "Erreur lors de la mise à jour.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                Toast.makeText(EditExerciseActivity.this, "Erreur de connexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Long exerciseId, Uri imageUri, Exercise exercise) {
        try {
            File file = FileUtil.from(this, imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadExerciseImage(exerciseId, body).enqueue(new Callback<Exercise>() {
                @Override
                public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                    if (response.isSuccessful()) {
                        exercise.setImageUrl(response.body().getImageUrl());
                        Log.d(TAG, "Image uploadée : " + exercise.getImageUrl());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                    Log.e(TAG, "Erreur lors de l'upload de l'image : ", t);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la conversion de l'image : ", e);
        }
    }

    private void uploadVideo(Long exerciseId, Uri videoUri, Exercise exercise) {
        try {
            File file = FileUtil.from(this, videoUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(videoUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadExerciseVideo(exerciseId, body).enqueue(new Callback<Exercise>() {
                @Override
                public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                    if (response.isSuccessful()) {
                        exercise.setVideoUrl(response.body().getVideoUrl());
                        Log.d(TAG, "Vidéo uploadée : " + exercise.getVideoUrl());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                    Log.e(TAG, "Erreur lors de l'upload de la vidéo : ", t);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la conversion de la vidéo : ", e);
        }
    }

    private void loadImageFromUrl(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e("IMAGE_LOADING", "L'URL de l'image est nulle ou vide");
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }

        // Construire l'URL complète si elle est partielle
        String fullImageUrl;
        if (imageUrl.startsWith("http")) {
            fullImageUrl = imageUrl; // Si l'URL est déjà complète, l'utiliser directement
        } else {
            fullImageUrl = "http://10.0.2.2:8080/api/exercises/images/"
                    + imageUrl.replaceFirst("^/+", "").replaceFirst("^uploads/images/", "");
        }

        Log.d("IMAGE_LOADING", "URL complète de l'image : " + fullImageUrl);

        Glide.with(this)
                .load(fullImageUrl + "?timestamp=" + System.currentTimeMillis()) // Ajout d'un timestamp pour éviter le cache
                .placeholder(android.R.drawable.ic_menu_gallery) // Image de chargement temporaire
                .error(android.R.drawable.ic_menu_close_clear_cancel) // Image en cas d'erreur
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("IMAGE_LOADING", "Échec du chargement de l'image", e);
                        return false; // Affiche l'image d'erreur par défaut
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d("IMAGE_LOADING", "Image chargée avec succès depuis : " + fullImageUrl);
                        return false; // Continue l'affichage normal
                    }
                })
                .into(imageView);
    }

    private void loadVideoFromUrl(VideoView videoView, String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            Log.e(TAG, "L'URL de la vidéo est nulle ou vide");
            videoView.setVisibility(View.GONE);
            return;
        }

        // Construire l'URL complète si elle est partielle
        String fullVideoUrl = videoUrl.startsWith("http") ? videoUrl : "http://10.0.2.2:8080" + videoUrl;

        Log.d(TAG, "URL complète de la vidéo : " + fullVideoUrl);

        try {
            videoView.setVideoURI(Uri.parse(fullVideoUrl));
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoView.start();
            });
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture de la vidéo", e);
            Toast.makeText(this, "Impossible de lire la vidéo", Toast.LENGTH_SHORT).show();
            videoView.setVisibility(View.GONE);
        }
    }

}