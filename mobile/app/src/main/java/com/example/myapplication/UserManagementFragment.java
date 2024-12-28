package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.UserAdapter;
import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserManagementFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialiser l'adaptateur avec le listener de suppression
        setupUserAdapter();

        // Charger les utilisateurs
        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Erreur lors du chargement des utilisateurs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour supprimer un utilisateur
    private void deleteUser(Long userId) {
        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Suppression réussie
                    userList.removeIf(user -> user.getId().equals(userId));
                    userAdapter.notifyDataSetChanged();

                    // Boîte de dialogue de confirmation
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Suppression réussie")
                            .setMessage("L'utilisateur a été supprimé avec succès.")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Configurer l'adaptateur
    private void setupUserAdapter() {
        userAdapter = new UserAdapter(requireContext(), userList, userId -> {
            // Afficher une boîte de dialogue de confirmation avant suppression
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cet utilisateur ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> deleteUser(userId))
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        recyclerView.setAdapter(userAdapter);
    }
}
