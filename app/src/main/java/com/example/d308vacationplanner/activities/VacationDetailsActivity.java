package com.example.d308vacationplanner.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.database.VacationRepository;
import com.example.d308vacationplanner.entities.Vacation;
import com.example.d308vacationplanner.receivers.VacationNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

public class VacationDetailsActivity extends AppCompatActivity {

    private VacationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Check for exact alarm permissions on Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            checkExactAlarmPermission();
        }
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
        int vacationId = getIntent().getIntExtra("id", -1);
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

            // Validate date format
            if (!isValidDate(updatedStartDate) || !isValidDate(updatedEndDate)) {
                Toast.makeText(this, "Invalid date format. Please use MM/dd/yy.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date logic
            if (!isEndDateAfterStartDate(updatedStartDate, updatedEndDate)) {
                Toast.makeText(this, "End date must be after the start date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create updated vacation object
            Vacation updatedVacation = new Vacation(vacationId, updatedTitle, updatedHotel, updatedStartDate, updatedEndDate);

            // Save updates to the database
            repository.updateVacation(updatedVacation);

            // Schedule notifications for vacation start and end dates
            scheduleNotifications(updatedVacation);

            Toast.makeText(this, "Vacation updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        });


        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }

    // Method to schedule vacation notifications
    private void scheduleNotifications(Vacation vacation) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

        try {
            Date startDate = sdf.parse(vacation.getStartDate());
            Date endDate = sdf.parse(vacation.getEndDate());

            if (startDate != null && endDate != null) {
                // Start date notification
                Intent startIntent = new Intent(this, VacationNotificationReceiver.class);
                startIntent.putExtra("title", "Vacation Alert");
                startIntent.putExtra("message", "Your vacation starts today!");
                PendingIntent startPendingIntent = PendingIntent.getBroadcast(
                        this,
                        vacation.getId(),
                        startIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // End date notification
                Intent endIntent = new Intent(this, VacationNotificationReceiver.class);
                endIntent.putExtra("title", "Vacation Alert");
                endIntent.putExtra("message", "Your vacation ends today!");
                PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                        this,
                        vacation.getId() + 1000,
                        endIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Schedule alarms
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, startDate.getTime(), startPendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, endDate.getTime(), endPendingIntent);
            }
        } catch (ParseException e) {
            Log.e("VacationDetailsActivity", "Error parsing dates", e);
        }
    }

    @RequiresApi(31) // For Android 12 and above
    private void checkExactAlarmPermission() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) {
            new AlertDialog.Builder(this)
                    .setTitle("Exact Alarm Permission Required")
                    .setMessage("This app needs permission to schedule exact alarms for vacation alerts.")
                    .setPositiveButton("Grant Permission", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // Validates if the date is in MM/dd/yy format
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Validates if the end date is after the start date
    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && start != null && end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }
}
