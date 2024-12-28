package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HomePagerAdapter homePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Initialiser l'adaptateur
        homePagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(homePagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer((page, position) -> {
            if (position == 0) {
                page.setAlpha(1); // Fragment visible
            } else {
                page.setAlpha(0); // Fragment masqué
            }
        });

        // Lier le TabLayout avec le ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setIcon(R.drawable.robot);
                    break;
                case 2:
                    tab.setIcon(R.drawable.list);
                    break;
                case 3:
                    tab.setIcon(R.drawable.time);
                    break;
                case 4:
                    tab.setIcon(R.drawable.profile);
                    break;
                case 5:
                    tab.setIcon(R.drawable.info);
                    break;
                default:
                    tab.setIcon(R.drawable.home);
                    break;
            }
        }).attach();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflater le menu de la Toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString title = new SpannableString(item.getTitle());
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), 0);
            item.setTitle(title);
            Drawable icon = item.getIcon();
            if (icon != null) {
                icon.setTint(getResources().getColor(R.color.black));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareApp();
            return true;
        } else if (id == R.id.action_settings) {
            // Action pour les paramètres
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

// Méthode pour naviguer vers un onglet spécifique
    public void navigateToTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Application");
        intent.putExtra(Intent.EXTRA_TEXT, "Try our application : https://example.com");
        startActivity(Intent.createChooser(intent, "share in"));
    }
}
