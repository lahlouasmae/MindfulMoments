package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.AcceuilAdminFragment;
import com.example.myapplication.AdminExerciseFragment;
import com.example.myapplication.UserManagementFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AcceuilAdminFragment(); // Gérer les utilisateurs
            case 1:
                return new UserManagementFragment(); // Gérer les utilisateurs
            case 2:
                return new AdminExerciseFragment(); // Gérer les exercices
            default:
                throw new IllegalArgumentException("Position invalide: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Deux fragments : utilisateurs et exercices
    }
}