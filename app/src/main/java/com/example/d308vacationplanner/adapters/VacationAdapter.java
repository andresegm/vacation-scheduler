package com.example.d308vacationplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.entities.Vacation;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private final List<Vacation> vacationList;
    private final OnVacationClickListener clickListener;

    public VacationAdapter(List<Vacation> vacationList, OnVacationClickListener clickListener) {
        this.vacationList = vacationList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);
        holder.titleTextView.setText(vacation.getTitle());
        holder.itemView.setOnClickListener(v -> clickListener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    public void updateData(List<Vacation> newVacations) {
        vacationList.clear(); // Clear existing data
        vacationList.addAll(newVacations); // Add new data
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnVacationClickListener {
        void onVacationClick(Vacation vacation);
    }
}
