package com.example.d308vacationplanner.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308vacationplanner.R;

public class VacationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Bind views
        TextView titleTextView = findViewById(R.id.vacation_title);
        TextView hotelTextView = findViewById(R.id.vacation_hotel);
        TextView startDateTextView = findViewById(R.id.vacation_start_date);
        TextView endDateTextView = findViewById(R.id.vacation_end_date);

        Button backButton = findViewById(R.id.button_back);
        Button editButton = findViewById(R.id.button_edit);

        // Set example vacation details
        titleTextView.setText("Spring Break");
        hotelTextView.setText("Grand Beach Resort");
        startDateTextView.setText("03/20/2024");
        endDateTextView.setText("03/27/2024");

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Edit button functionality (navigate to edit screen)
        editButton.setOnClickListener(v -> {
            // Add navigation logic to edit screen later
        });
    }
}
