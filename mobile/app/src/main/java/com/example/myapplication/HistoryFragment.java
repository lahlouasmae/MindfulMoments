package com.example.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ExerciseAdapter;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.ExerciseUpdateEvent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private TextView stressAverage;
    private static final String TAG = "HistoryFragment";
    private HistoryViewModel viewModel;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "stress_alert_channel";
    private RecyclerView recyclerView;
    private LineChart stressChart;
    private ExerciseAdapter adapter;
    private final List<Exercise> completedExercises = new ArrayList<>();
    private SearchView searchView;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPermissionLauncher();
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(requireContext(),
                                "Les notifications sont désactivées",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        Long userId = prefs.getLong("USER_ID", -1);
        if (userId != -1) {
            viewModel.loadHistorique();
            viewModel.fetchStressData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        stressChart = view.findViewById(R.id.stressChart);
        stressAverage = view.findViewById(R.id.stressAverage);

        setupViews(view);
        setupSearchView();
        setupStressChart();
        setupObservers();
        checkNotificationPermission();
        createNotificationChannel();

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        Long userId = prefs.getLong("USER_ID", -1);
        Log.d(TAG, "UserId récupéré: " + userId);

        if (userId != -1) {
            viewModel.setUserId(userId);
            viewModel.loadHistorique();
            viewModel.fetchStressData();
        } else {
            Toast.makeText(requireContext(), "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void setupObservers() {
        viewModel.getExercises().observe(getViewLifecycleOwner(), exercises -> {
            completedExercises.clear();
            completedExercises.addAll(exercises);
            adapter.notifyDataSetChanged();
        });

        viewModel.getStressData().observe(getViewLifecycleOwner(), this::updateStressChart);

        viewModel.getStressAverage().observe(getViewLifecycleOwner(), average -> {
            if (average > 50.0) {
                showStressNotification(average);
            }
            stressAverage.setText(String.format("Stress average : %.1f", average));
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertes de stress",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal pour les alertes de niveau de stress élevé");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000});
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setBypassDnd(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager =
                    requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showStressNotification(float average) {
        Intent intent = new Intent(requireContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("⚠️ Alert")
                .setContentText("High stress level : " + String.format("%.1f", average))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(new long[]{500, 500, 500})
                .setLights(Color.RED, 1000, 1000);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void setupViews(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExerciseAdapter(requireContext(), completedExercises, this::onExerciseToggled);
        recyclerView.setAdapter(adapter);
    }

    private void setupStressChart() {
        stressChart.setBackgroundColor(Color.WHITE);
        stressChart.getDescription().setEnabled(false);
        stressChart.setTouchEnabled(true);
        stressChart.setDragEnabled(true);
        stressChart.setScaleEnabled(true);
        stressChart.setPinchZoom(true);
        stressChart.setDrawGridBackground(false);
        stressChart.setViewPortOffsets(60f, 20f, 30f, 50f);

        XAxis xAxis = stressChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.GRAY);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);

        YAxis leftAxis = stressChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.GRAY);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(10f);

        stressChart.getAxisRight().setEnabled(false);
        stressChart.getLegend().setEnabled(true);
        stressChart.getLegend().setTextColor(Color.BLACK);
        stressChart.getLegend().setTextSize(12f);
    }

    private void updateStressChart(List<Object> stressData) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        try {
            List<Entry> entries = new ArrayList<>();
            float maxY = 0f;

            for (int i = 0; i < stressData.size(); i++) {
                try {
                    Object data = stressData.get(i);
                    float stressLevel;
                    if (data instanceof Number) {
                        stressLevel = ((Number) data).floatValue();
                    } else {
                        stressLevel = Float.parseFloat(data.toString());
                    }
                    entries.add(new Entry(i, stressLevel));
                    maxY = Math.max(maxY, stressLevel);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Erreur de conversion pour l'entrée " + i, e);
                }
            }

            if (entries.isEmpty()) {
                Log.d(TAG, "Aucune donnée de stress valide");
                return;
            }

            LineDataSet dataSet = new LineDataSet(entries, "Stress Level ");
            dataSet.setColor(getResources().getColor(R.color.purple_500));
            dataSet.setLineWidth(2f);
            dataSet.setCircleColor(getResources().getColor(R.color.purple_500));
            dataSet.setCircleRadius(4f);
            dataSet.setDrawCircles(true);
            dataSet.setDrawCircleHole(false);
            dataSet.setDrawValues(true);
            dataSet.setValueTextSize(10f);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setMode(LineDataSet.Mode.LINEAR);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getResources().getColor(R.color.purple_200));
            dataSet.setFillAlpha(50);

            XAxis xAxis = stressChart.getXAxis();
            xAxis.setDrawLabels(false);
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(true);
            xAxis.setLabelCount(entries.size());

            YAxis leftAxis = stressChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(maxY + 1f);
            leftAxis.setDrawGridLines(true);
            leftAxis.enableGridDashedLine(10f, 10f, 0f);

            LineData lineData = new LineData(dataSet);
            stressChart.setData(lineData);
            stressChart.setVisibleXRangeMaximum(7);
            stressChart.moveViewToX(0);
            stressChart.invalidate();
            stressChart.animateX(1000);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la mise à jour du graphique", e);
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.updateList(viewModel.filterExercises(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.updateList(viewModel.filterExercises(newText));
                return true;
            }
        });
    }

    private void onExerciseToggled(Exercise exercise) {
        viewModel.removeExercise(exercise);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExerciseUpdated(ExerciseUpdateEvent event) {
            if (event == null || !isAdded()) {
                return;
            }

            Exercise updatedExercise = event.getExercise();
            if (updatedExercise != null && !updatedExercise.isCompleted()) {
                for (int i = 0; i < completedExercises.size(); i++) {
                    if (completedExercises.get(i).getId().equals(updatedExercise.getId())) {
                        completedExercises.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
        }
    }