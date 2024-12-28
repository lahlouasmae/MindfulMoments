package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.adapter.AdminPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private static final String TAG = "AdminHomeActivity";
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity created");
        setContentView(R.layout.activity_admin_home);

        try {
            viewPager = findViewById(R.id.viewPager);
            bottomNavigationView = findViewById(R.id.bottomNavigation);

            Log.d(TAG, "onCreate: Initializing ViewPager and BottomNavigationView");

            AdminPagerAdapter adapter = new AdminPagerAdapter(this);
            viewPager.setAdapter(adapter);

            // Disable swipe
            viewPager.setUserInputEnabled(false);

            // Setup BottomNavigationView with ViewPager2
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int position;
                if (item.getItemId() == R.id.navigation_home) {
                    position = 0;
                } else if (item.getItemId() == R.id.navigation_users) {
                    position = 1;
                } else if (item.getItemId() == R.id.navigation_exercises) {
                    position = 2;
                } else {
                    return false;
                }
                viewPager.setCurrentItem(position, false);
                return true;
            });

            // Setup ViewPager2 to update BottomNavigationView
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    int itemId;
                    switch (position) {
                        case 0:
                            itemId = R.id.navigation_home;
                            break;
                        case 1:
                            itemId = R.id.navigation_users;
                            break;
                        case 2:
                            itemId = R.id.navigation_exercises;
                            break;
                        default:
                            return;
                    }
                    bottomNavigationView.setSelectedItemId(itemId);
                }
            });

            // Handle intent extras for navigation
            boolean shouldOpenExercises = getIntent().getBooleanExtra("open_exercises", false);
            if (shouldOpenExercises) {
                viewPager.setCurrentItem(2, false);
            }

            Log.d(TAG, "onCreate: ViewPager and BottomNavigationView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error initializing AdminHomeActivity", e);
        }
    }

    public void navigateToTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Cleaning up resources");
    }
}