package com.example.d308vacationplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
        adapter = new VacationAdapter(new ArrayList<>(), new VacationAdapter.OnVacationActionListener() {
            @Override
            public void onVacationClick(Vacation vacation) {
                // Navigate to VacationDetailsActivity on item click
                Intent intent = new Intent(MainActivity.this, VacationDetailsActivity.class);
                intent.putExtra("id", vacation.getId());
                intent.putExtra("title", vacation.getTitle());
                intent.putExtra("hotel", vacation.getHotel());
                intent.putExtra("startDate", vacation.getStartDate());
                intent.putExtra("endDate", vacation.getEndDate());
                startActivity(intent);
            }

            @Override
            public void onVacationDelete(Vacation vacation) {
                // Handle vacation deletion
                repository.deleteVacation(vacation, new VacationRepository.DeletionCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Vacation deleted successfully", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onFailure(String message) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
        vacationList.setAdapter(adapter);

        // Observe LiveData from the repository
        repository.getAllVacations().observe(this, vacations -> {
            // Update adapter when data changes
            if (vacations != null) {
                adapter.updateData(vacations);
            }
        });

        // Bind Add Vacation Button
        Button addVacationButton = findViewById(R.id.add_vacation_button);
        addVacationButton.setOnClickListener(v -> {
            // Add a new vacation to the database
            Vacation newVacation = new Vacation(0, "New Vacation", "Hotel Name", "01/01/2025", "01/07/2025");
            repository.insertVacation(newVacation);
            Toast.makeText(this, "Vacation added", Toast.LENGTH_SHORT).show();
        });
    }
}
