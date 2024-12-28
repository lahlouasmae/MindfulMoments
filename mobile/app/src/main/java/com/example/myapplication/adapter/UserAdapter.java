package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<User> users;
    private final OnUserDeleteListener deleteListener;
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static final String TAG = "UserAdapter";

    public UserAdapter(Context context, List<User> users, OnUserDeleteListener deleteListener) {
        this.context = context;
        this.users = users;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.nameTextView.setText(user.getFirstName() + " " + user.getLastName());

        // Chargement de l'image de l'utilisateur
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            // Nettoyer l'URL de l'image en supprimant les éventuels chemins en double
            String imageUrl = user.getImageUrl();
            if (imageUrl.startsWith("/")) {
                imageUrl = imageUrl.substring(1);
            }
            if (imageUrl.startsWith("uploads/")) {
                imageUrl = BASE_URL + imageUrl;
            } else {
                imageUrl = BASE_URL + "uploads/users/" + imageUrl;
            }

            // Log pour le débogage
            Log.d(TAG, "Loading image from URL: " + imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .circleCrop())
                    .into(holder.userImageView);
        } else {
            Log.d(TAG, "No image URL available for user: " + user.getId());
            // Image par défaut
            Glide.with(context)
                    .load(R.drawable.default_image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.userImageView);
        }

        // Gestion du bouton Supprimer
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onUserDelete(user.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView, nameTextView;
        Button deleteButton;
        ImageView userImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            userImageView = itemView.findViewById(R.id.userImageView);
        }
    }

    public interface OnUserDeleteListener {
        void onUserDelete(Long userId);
    }
}