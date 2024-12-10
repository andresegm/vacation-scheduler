package com.example.d308vacationplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.database.VacationRepository;
import com.example.d308vacationplanner.entities.Vacation;

public class VacationDetailsActivity extends AppCompatActivity {

    private VacationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Initialize repository
        repository = new VacationRepository(getApplicationContext());

        // Bind views
        EditText titleEditText = findViewById(R.id.vacation_title);
        EditText hotelEditText = findViewById(R.id.vacation_hotel);
        EditText startDateEditText = findViewById(R.id.vacation_start_date);
        EditText endDateEditText = findViewById(R.id.vacation_end_date);

        Button saveButton = findViewById(R.id.button_save);
        Button backButton = findViewById(R.id.button_back);

        // Retrieve vacation details from Intent
        int vacationId = getIntent().getIntExtra("id", -1); // Ensure ID is passed from MainActivity
        String title = getIntent().getStringExtra("title");
        String hotel = getIntent().getStringExtra("hotel");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");

        // Populate fields with existing data
        titleEditText.setText(title);
        hotelEditText.setText(hotel);
        startDateEditText.setText(startDate);
        endDateEditText.setText(endDate);

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            String updatedTitle = titleEditText.getText().toString();
            String updatedHotel = hotelEditText.getText().toString();
            String updatedStartDate = startDateEditText.getText().toString();
            String updatedEndDate = endDateEditText.getText().toString();

            // Create updated vacation object
            Vacation updatedVacation = new Vacation(vacationId, updatedTitle, updatedHotel, updatedStartDate, updatedEndDate);

            // Log the updated vacation details
            Log.d("VacationDetailsActivity", "Saving updated vacation: ID=" + updatedVacation.getId() +
                    ", Title=" + updatedVacation.getTitle());

            // Save updates to the database
            repository.updateVacation(updatedVacation);
            Toast.makeText(this, "Vacation updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        });

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }
}
