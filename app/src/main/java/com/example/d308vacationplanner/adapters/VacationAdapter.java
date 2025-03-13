package com.example.d308vacationplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private final List<Vacation> vacationList;
    private final OnVacationActionListener actionListener;

    public interface OnVacationActionListener {
        void onVacationClick(Vacation vacation);
        void onVacationDelete(Vacation vacation);
    }

    public VacationAdapter(List<Vacation> vacationList, OnVacationActionListener actionListener) {
        this.vacationList = vacationList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacation_item, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);
        holder.titleTextView.setText(vacation.getTitle());

        // Handle click actions
        holder.itemView.setOnClickListener(v -> actionListener.onVacationClick(vacation));
        holder.deleteButton.setOnClickListener(v -> actionListener.onVacationDelete(vacation));
    }

    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    public void updateData(List<Vacation> newVacations) {
        vacationList.clear();
        vacationList.addAll(newVacations);
        notifyDataSetChanged();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final Button deleteButton;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.vacation_title);
            deleteButton = itemView.findViewById(R.id.vacation_delete_button);
        }
    }
}
