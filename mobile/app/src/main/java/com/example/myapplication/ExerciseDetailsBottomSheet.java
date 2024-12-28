package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.myapplication.model.Exercise;

public class ExerciseDetailsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "ExerciseDetailsBottomSheet";
    private final Exercise exercise;

    public ExerciseDetailsBottomSheet(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details_bottom_sheet, container, false);

        // Initialisation des vues
        TextView nameText = view.findViewById(R.id.detailsNameText);
        TextView typeText = view.findViewById(R.id.detailsTypeText);
        TextView stressLevelText = view.findViewById(R.id.detailsStressLevelText);
        TextView durationText = view.findViewById(R.id.detailsDurationText);
        TextView caloriesText = view.findViewById(R.id.detailsCaloriesText);
        TextView difficultyText = view.findViewById(R.id.detailsDifficultyText);
        TextView descriptionText = view.findViewById(R.id.detailsDescriptionText);
        TextView instructionsText = view.findViewById(R.id.detailsInstructionsText);
        TextView benefitsText = view.findViewById(R.id.detailsBenefitsText);
        ImageView imageView = view.findViewById(R.id.exerciseImageView);
        VideoView videoView = view.findViewById(R.id.exerciseVideoView);

        // Mise à jour des champs texte
        nameText.setText(exercise.getName() != null ? exercise.getName() : "Nom non disponible");
        typeText.setText(exercise.getType() != null ? exercise.getType() : "Type non disponible");
        stressLevelText.setText(String.format(" %d - %d",
                exercise.getMinStressLevel() != null ? exercise.getMinStressLevel() : 0,
                exercise.getMaxStressLevel() != null ? exercise.getMaxStressLevel() : 0));
        durationText.setText(String.format("Durée: %d minutes", exercise.getDuration() != null ? exercise.getDuration() : 0));
        caloriesText.setText(String.format("Calories: %d", exercise.getCalories() != null ? exercise.getCalories() : 0));
        difficultyText.setText(String.format("Difficulté: %s", exercise.getDifficulty() != null ? exercise.getDifficulty() : "Inconnue"));
        descriptionText.setText(exercise.getDescription() != null ? exercise.getDescription() : "Description non disponible");
        instructionsText.setText(exercise.getInstructions() != null ? exercise.getInstructions() : "Instructions non disponibles");
        benefitsText.setText(exercise.getBenefits() != null ? exercise.getBenefits() : "Bénéfices non disponibles");

        // Chargement de l'image
        loadImageFromUrl(imageView, exercise.getImageUrl());

        // Chargement de la vidéo
        loadVideoFromUrl(videoView, exercise.getVideoUrl());

        return view;
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

        String fullVideoUrl = videoUrl.startsWith("http") ? videoUrl : "http://10.0.2.2:8080" + videoUrl;

        try {
            videoView.setVideoURI(Uri.parse(fullVideoUrl));
            videoView.setOnPreparedListener(mp -> mp.setLooping(true));
            videoView.start();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture de la vidéo", e);
            Toast.makeText(getContext(), "Impossible de lire la vidéo", Toast.LENGTH_SHORT).show();
            videoView.setVisibility(View.GONE);
        }
    }
}