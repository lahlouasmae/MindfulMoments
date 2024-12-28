package com.example.myapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Exercise;

import java.util.List;
public class AdminExerciseAdapter extends RecyclerView.Adapter<AdminExerciseAdapter.ExerciseViewHolder> {
    private static final String TAG = "AdminExerciseAdapter";
    private final List<Exercise> exercises;
    private final Context context;
    private final OnExerciseClickListener clickListener;
    private final OnExerciseActionListener actionListener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public interface OnExerciseActionListener {
        void onEdit(Exercise exercise);
        void onDelete(Exercise exercise);
    }

    public AdminExerciseAdapter(Context context, List<Exercise> exercises,
                                OnExerciseClickListener clickListener,
                                OnExerciseActionListener actionListener) {
        this.context = context;
        this.exercises = exercises;
        this.clickListener = clickListener;
        this.actionListener = actionListener;
        Log.d(TAG, "Adapter created with " + (exercises != null ? exercises.size() : 0) + " exercises");
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_admin, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        Log.d(TAG, "Binding exercise at position " + position + ": " + exercise.getName());

        holder.nameText.setText(exercise.getName());
        holder.typeText.setText(exercise.getType());


        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                Log.d(TAG, "Click on exercise: " + exercise.getId());
                clickListener.onExerciseClick(exercise);
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            if (actionListener != null) {
                Log.d(TAG, "Long click on exercise: " + exercise.getId());
                actionListener.onEdit(exercise);
                return true;
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameText, typeText;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            nameText = itemView.findViewById(R.id.exerciseNameText);  // Changé pour correspondre au layout
            typeText = itemView.findViewById(R.id.exerciseTypeText);  // Changé pour correspondre au layout
        }
    }

    // Les autres méthodes restent inchangées...
    public Exercise getExerciseAt(int position) {
        if (exercises != null && position >= 0 && position < exercises.size()) {
            return exercises.get(position);
        }
        return null;
    }

    public void updateExercise(Exercise updatedExercise) {
        if (updatedExercise != null && updatedExercise.getId() != null) {
            for (int i = 0; i < exercises.size(); i++) {
                if (exercises.get(i).getId().equals(updatedExercise.getId())) {
                    exercises.set(i, updatedExercise);
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    public void removeItem(int position) {
        if (exercises != null && position >= 0 && position < exercises.size()) {
            exercises.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restoreItem(Exercise exercise, int position) {
        if (exercises != null && position >= 0 && position <= exercises.size()) {
            exercises.add(position, exercise);
            notifyItemInserted(position);
        }
    }

    public void updateData(List<Exercise> newExercises) {
        if (exercises != null) {
            exercises.clear();
            if (newExercises != null) {
                exercises.addAll(newExercises);
            }
            notifyDataSetChanged();
        }
    }
    public void resetPosition(int position) {
        notifyItemChanged(position);
    }
}