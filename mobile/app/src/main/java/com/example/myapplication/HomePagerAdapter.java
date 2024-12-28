package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AcceuilFragment();
            case 1:
                return new RecommendationsFragment();
            case 2:
                return new ExerciseFragment();
            case 3:
                return new HistoryFragment();
            case 4:
                return new ProfileFragment();
            case 5:
                return new AboutUsFragment();
            default:
                return new RecommendationsFragment();
        }
    }

    @Override
    public long getItemId(int position) {
        return position; // Utilisez la position comme identifiant unique
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < getItemCount();
    }


    @Override
    public int getItemCount() {
        return 6; // Augmenté à 4 pour inclure le fragment profil
    }
}