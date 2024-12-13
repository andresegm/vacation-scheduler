package com.example.d308vacationplanner.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.adapters.ExcursionAdapter;
import com.example.d308vacationplanner.database.VacationRepository;
import com.example.d308vacationplanner.entities.Excursion;
import com.example.d308vacationplanner.entities.Vacation;
import com.example.d308vacationplanner.receivers.VacationNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VacationDetailsActivity extends AppCompatActivity {

    private VacationRepository repository;
    private ExcursionAdapter excursionAdapter;
    private int vacationId;

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
        Button shareButton = findViewById(R.id.button_share);
        Button backButton = findViewById(R.id.button_back);
        Button addExcursionButton = findViewById(R.id.button_add_excursion);

        // RecyclerView for excursions
        RecyclerView excursionList = findViewById(R.id.excursion_list);
        excursionList.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(new ArrayList<>(), this::handleExcursionAction);
        excursionList.setAdapter(excursionAdapter);

        // Retrieve vacation details from Intent
        vacationId = getIntent().getIntExtra("id", -1);
        String title = getIntent().getStringExtra("title");
        String hotel = getIntent().getStringExtra("hotel");
        String startDate = getIntent().getStringExtra("startDate");
        String endDate = getIntent().getStringExtra("endDate");

        // Populate fields with existing data
        titleEditText.setText(title);
        hotelEditText.setText(hotel);
        startDateEditText.setText(startDate);
        endDateEditText.setText(endDate);

        // Observe excursions for this vacation
        repository.getExcursionsForVacation(vacationId).observe(this, excursions -> {
            if (excursions != null) {
                excursionAdapter.updateData(excursions);
            }
        });

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            String updatedTitle = titleEditText.getText().toString();
            String updatedHotel = hotelEditText.getText().toString();
            String updatedStartDate = startDateEditText.getText().toString();
            String updatedEndDate = endDateEditText.getText().toString();

            if (!isValidDate(updatedStartDate) || !isValidDate(updatedEndDate)) {
                Toast.makeText(this, "Invalid date format. Please use MM/dd/yy.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEndDateAfterStartDate(updatedStartDate, updatedEndDate)) {
                Toast.makeText(this, "End date must be after the start date.", Toast.LENGTH_SHORT).show();
                return;
            }

            Vacation updatedVacation = new Vacation(vacationId, updatedTitle, updatedHotel, updatedStartDate, updatedEndDate);

            repository.updateVacation(updatedVacation);
            scheduleNotifications(updatedVacation);
            Toast.makeText(this, "Vacation updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Share button functionality
        shareButton.setOnClickListener(v -> {
            String vacationDetails = "Vacation Details:\n" +
                    "Title: " + titleEditText.getText().toString() + "\n" +
                    "Hotel: " + hotelEditText.getText().toString() + "\n" +
                    "Start Date: " + startDateEditText.getText().toString() + "\n" +
                    "End Date: " + endDateEditText.getText().toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);

            startActivity(Intent.createChooser(shareIntent, "Share Vacation Details"));
        });

        // Add Excursion button functionality
        addExcursionButton.setOnClickListener(v -> {
            Excursion newExcursion = new Excursion(0, "New Excursion", "12/25/2024", vacationId);
            repository.insertExcursion(newExcursion);
            Toast.makeText(this, "Excursion added", Toast.LENGTH_SHORT).show();
        });

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }

    private void handleExcursionAction(Excursion excursion, String action) {
        switch (action) {
            case "DELETE":
                repository.deleteExcursion(excursion);
                Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
                break;
            case "EDIT":
                // Logic for editing excursions
                Toast.makeText(this, "Edit functionality not implemented yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void scheduleNotifications(Vacation vacation) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

        try {
            Date startDate = sdf.parse(vacation.getStartDate());
            Date endDate = sdf.parse(vacation.getEndDate());

            if (startDate != null && endDate != null) {
                Intent startIntent = new Intent(this, VacationNotificationReceiver.class);
                startIntent.putExtra("title", "Vacation Alert");
                startIntent.putExtra("message", "Your vacation starts today!");
                PendingIntent startPendingIntent = PendingIntent.getBroadcast(
                        this,
                        vacation.getId(),
                        startIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                Intent endIntent = new Intent(this, VacationNotificationReceiver.class);
                endIntent.putExtra("title", "Vacation Alert");
                endIntent.putExtra("message", "Your vacation ends today!");
                PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                        this,
                        vacation.getId() + 1000,
                        endIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, startDate.getTime(), startPendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, endDate.getTime(), endPendingIntent);
            }
        } catch (ParseException e) {
            Log.e("VacationDetailsActivity", "Error parsing dates", e);
        }
    }

    @RequiresApi(31)
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
