package com.example.d308vacationplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.activities.VacationDetailsActivity;
import com.example.d308vacationplanner.adapters.VacationAdapter;
import com.example.d308vacationplanner.database.VacationRepository;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private VacationAdapter adapter;
    private VacationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Repository
        repository = new VacationRepository(getApplicationContext());

        // Bind RecyclerView
        RecyclerView vacationList = findViewById(R.id.vacation_list);
        vacationList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter with an empty list
        adapter = new VacationAdapter(new ArrayList<>(), vacation -> {
            // Navigate to VacationDetailsActivity on item click
            Intent intent = new Intent(MainActivity.this, VacationDetailsActivity.class);
            intent.putExtra("id", vacation.getId());
            intent.putExtra("title", vacation.getTitle());
            intent.putExtra("hotel", vacation.getHotel());
            intent.putExtra("startDate", vacation.getStartDate());
            intent.putExtra("endDate", vacation.getEndDate());
            startActivity(intent);
        });
        vacationList.setAdapter(adapter);

        // Observe LiveData from the repository
        repository.getAllVacations().observe(this, vacations -> {
            // Update adapter when data changes
            if (vacations != null) {
                adapter.updateData(vacations);
            }
        });
    }
}
