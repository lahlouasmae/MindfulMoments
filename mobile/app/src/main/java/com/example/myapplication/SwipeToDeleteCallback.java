package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;



public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final SwipeActionCallback callback;

    public SwipeToDeleteCallback(SwipeActionCallback callback) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.callback = callback;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            callback.onDeleteSwipe(position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            callback.onEditSwipe(position);
        }
    }
}