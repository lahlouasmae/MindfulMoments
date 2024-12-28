package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExerciseActivity extends AppCompatActivity {

    private static final String TAG = "AddExerciseActivity";

    private EditText nameEditText, typeEditText, minStressEditText, maxStressEditText;
    private EditText durationEditText, caloriesEditText, difficultyEditText;
    private EditText descriptionEditText, instructionsEditText, benefitsEditText;
    private Button saveButton, selectImageButton, selectVideoButton;
    private TextView selectedImageText, selectedVideoText;
    private ApiService apiService;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private Uri selectedImageUri;
    private Uri selectedVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        initializeViews();

        apiService = ApiClient.getClient().create(ApiService.class);

        selectImageButton.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        selectVideoButton.setOnClickListener(v -> openFileChooser(PICK_VIDEO_REQUEST));

        saveButton.setOnClickListener(this::saveExercise);
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        typeEditText = findViewById(R.id.typeEditText);
        minStressEditText = findViewById(R.id.minStressLevelEditText);
        maxStressEditText = findViewById(R.id.maxStressLevelEditText);
        durationEditText = findViewById(R.id.durationEditText);
        caloriesEditText = findViewById(R.id.caloriesEditText);
        difficultyEditText = findViewById(R.id.difficultyEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        benefitsEditText = findViewById(R.id.benefitsEditText);
        saveButton = findViewById(R.id.saveButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectVideoButton = findViewById(R.id.selectVideoButton);
        selectedImageText = findViewById(R.id.selectedImageText);
        selectedVideoText = findViewById(R.id.selectedVideoText);
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(requestCode == PICK_IMAGE_REQUEST ? "image/" : "video/");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                selectedImageText.setText(FileUtil.getFileName(this, selectedImageUri));
                Log.d(TAG, "Image sélectionnée : " + selectedImageUri);
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                selectedVideoText.setText(FileUtil.getFileName(this, selectedVideoUri));
                Log.d(TAG, "Vidéo sélectionnée : " + selectedVideoUri);
            }
        }
    }

    public void saveExercise(View view) {
        Exercise exercise = collectExerciseDetails();

        if (exercise == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.addExercise(exercise).enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Exercise savedExercise = response.body();
                    Log.d(TAG, "Exercice ajouté avec ID : " + savedExercise.getId());

                    // Gérer les uploads en séquence
                    if (selectedImageUri != null) {
                        uploadImage(savedExercise.getId(), savedExercise);
                    } else if (selectedVideoUri != null) {
                        uploadVideo(savedExercise.getId(), savedExercise);
                    } else {
                        redirectToExerciseList();
                    }
                } else {
                    Log.e(TAG, "Erreur lors de l'ajout : " + response.message());
                    Toast.makeText(AddExerciseActivity.this, "Erreur lors de l'ajout.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                Log.e(TAG, "Erreur réseau : ", t);
                Toast.makeText(AddExerciseActivity.this, "Erreur réseau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Exercise collectExerciseDetails() {
        String name = nameEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String minStress = minStressEditText.getText().toString();
        String maxStress = maxStressEditText.getText().toString();
        String duration = durationEditText.getText().toString();
        String calories = caloriesEditText.getText().toString();
        String difficulty = difficultyEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String instructions = instructionsEditText.getText().toString();
        String benefits = benefitsEditText.getText().toString();

        if (name.isEmpty() || type.isEmpty() || minStress.isEmpty() || maxStress.isEmpty() ||
                duration.isEmpty() || calories.isEmpty() || difficulty.isEmpty() ||
                description.isEmpty() || instructions.isEmpty() || benefits.isEmpty()) {
            return null;
        }

        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setType(type);
        exercise.setMinStressLevel(Integer.parseInt(minStress));
        exercise.setMaxStressLevel(Integer.parseInt(maxStress));
        exercise.setDuration(Integer.parseInt(duration));
        exercise.setCalories(Integer.parseInt(calories));
        exercise.setDifficulty(difficulty);
        exercise.setDescription(description);
        exercise.setInstructions(instructions);
        exercise.setBenefits(benefits);
        return exercise;
    }

    private void uploadImage(Long exerciseId, Exercise exercise) {
        try {
            File file = FileUtil.from(this, selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadExerciseImage(exerciseId, body).enqueue(new Callback<Exercise>() {
                @Override
                public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        exercise.setImageUrl(response.body().getImageUrl());
                        Log.d(TAG, "Image URL mise à jour : " + exercise.getImageUrl());

                        // Après l'upload de l'image, vérifier s'il y a une vidéo à uploader
                        if (selectedVideoUri != null) {
                            uploadVideo(exerciseId, exercise);
                        } else {
                            redirectToExerciseList();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Exercise> call, @NonNull Throwable t) {
                    Log.e(TAG, "Erreur lors de l'upload de l'image : ", t);
                    Toast.makeText(AddExerciseActivity.this, "Erreur lors de l'upload de l'image", Toast.LENGTH_SHORT).show();
                    // En cas d'erreur, on continue avec la vidéo si elle existe
                    if (selectedVideoUri != null) {
                        uploadVideo(exerciseId, exercise);
                    } else {
                        redirectToExerciseList();
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la conversion de l'image : ", e);
            // En cas d'erreur, on continue avec la vidéo si elle existe
            if (selectedVideoUri != null) {
                uploadVideo(exerciseId, exercise);
            } else {
                redirectToExerciseList();
            }
        }
    }

    private void uploadVideo(Long exerciseId, Exercise exercise) {
        try {
            File file = FileUtil.from(this, selectedVideoUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedVideoUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadExerciseVideo(exerciseId, body).enqueue(new Callback<Exercise>() {
                @Override
                public void onResponse(@NonNull Call<Exercise> call, @NonNull Response<Exercise> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        exercise.setVideoUrl(response.body().getVideoUrl());
                        Log.d(TAG, "Vidéo URL mise à jour : " + exercise.getVideoUrl());
                    }
                    redirectToExerciseList();
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

    private void redirectToExerciseList() {
        Intent intent = new Intent();
        intent.putExtra("open_exercises", true); // Passez un signal pour ouvrir AdminExerciseFragment
        setResult(RESULT_OK, intent);
        finish(); // Ferme l'activité actuelle et retourne le résultat
    }


}