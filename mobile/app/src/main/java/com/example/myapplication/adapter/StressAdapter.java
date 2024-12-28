package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class StressAdapter extends RecyclerView.Adapter<StressAdapter.ViewHolder> {

    private final Context context;
    private final List<String> stressLevels;

    public StressAdapter(Context context, List<String> stressLevels) {
        this.context = context;
        this.stressLevels = stressLevels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stress_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String stressLevel = stressLevels.get(position);
        holder.stressTextView.setText(stressLevel);
    }

    @Override
    public int getItemCount() {
        return stressLevels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stressTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            stressTextView = itemView.findViewById(R.id.stressTextView);
        }
    }
}
