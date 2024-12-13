package com.example.d308vacationplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> excursions;
    private final OnExcursionActionListener actionListener;

    // Listener interface for actions
    public interface OnExcursionActionListener {
        void onExcursionAction(Excursion excursion, String action);
    }

    // Constructor
    public ExcursionAdapter(List<Excursion> excursions, OnExcursionActionListener actionListener) {
        this.excursions = excursions;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.excursion_item, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursions.get(position);
        holder.titleTextView.setText(excursion.getTitle());
        holder.dateTextView.setText(excursion.getDate());

        // Handle delete button
        holder.deleteButton.setOnClickListener(v -> actionListener.onExcursionAction(excursion, "DELETE"));

        // Handle edit button
        holder.editButton.setOnClickListener(v -> actionListener.onExcursionAction(excursion, "EDIT"));
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public void updateData(List<Excursion> newExcursions) {
        excursions.clear();
        excursions.addAll(newExcursions);
        notifyDataSetChanged();
    }

    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        Button deleteButton;
        Button editButton;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.excursion_title);
            dateTextView = itemView.findViewById(R.id.excursion_date);
            deleteButton = itemView.findViewById(R.id.delete_excursion_button);
            editButton = itemView.findViewById(R.id.edit_excursion_button);
        }
    }
}
