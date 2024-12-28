package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ExerciseDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.ExerciseUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private static final String TAG = "ExerciseAdapter";
    private final Context context;
    private List<Exercise> exercises;
    private final OnExerciseToggleListener toggleListener;
    private final ApiService apiService;

    public interface OnExerciseToggleListener {
        void onToggle(Exercise exercise);
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseToggleListener toggleListener) {
        this.context = context;
        this.exercises = exercises;
        this.toggleListener = toggleListener;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void updateList(List<Exercise> newList) {
        this.exercises = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        Log.d(TAG, "Affichage de l'exercice : " + exercise.getName() + " | Terminé : " + exercise.isCompleted());

        // Set exercise details
        holder.nameTextView.setText(exercise.getName());
        holder.typeTextView.setText(exercise.getType());
        holder.statusTextView.setText(exercise.isCompleted() ? "completed" : "to do");
        holder.statusTextView.setTextColor(exercise.isCompleted() ?
                Color.parseColor("#4CAF50") : Color.parseColor("#FF9800"));

        // Toggle status on click
        holder.statusTextView.setOnClickListener(v -> {
            if (toggleListener != null) {
                toggleListener.onToggle(exercise);
            }
        });

        // Navigate to detail activity
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExerciseDetailActivity.class);
            intent.putExtra("exerciseId", exercise.getId());
            intent.putExtra("exercise", exercise);
            context.startActivity(intent);
        });
        TextView numberText = holder.itemView.findViewById(R.id.numberText);
        numberText.setText(String.valueOf(position + 1));

        // Modify this part
        View topProgressBar = holder.itemView.findViewById(R.id.topProgressBar);

        int color;
        switch (position % 3) {
            case 0:
                color = Color.parseColor("#E0D0FD"); // Rouge
                break;
            case 1:
                color = Color.parseColor("#E0D0FD"); // Orange
                break;
            default:
                color = Color.parseColor("#E0D0FD"); // Vert
                break;
        }

        // Create new GradientDrawable instead of casting
        GradientDrawable circleBackground = new GradientDrawable();
        circleBackground.setShape(GradientDrawable.OVAL);
        circleBackground.setColor(color);
        circleBackground.setStroke(0, Color.TRANSPARENT); // Supprime la bordure

// Set the background with shadow removal
        numberText.setBackground(circleBackground);
        numberText.setShadowLayer(0, 0, 0, Color.TRANSPARENT); // Supprime l'ombre
        numberText.setTextColor(Color.WHITE); // Assure que le texte est blanc

// Set the progress bar color
        topProgressBar.setBackgroundTintList(ColorStateList.valueOf(color));

    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameTextView, typeTextView, statusTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}