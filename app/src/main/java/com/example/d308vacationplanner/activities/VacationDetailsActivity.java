package com.example.d308vacationplanner.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.d308vacationplanner.receivers.ExcursionNotificationReceiver;
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

        // Initialize the repository
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

        // Setup RecyclerView for excursions
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

        // Save button functionality: Save updated vacation details
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

            // Validate date order
            if (!isEndDateAfterStartDate(updatedStartDate, updatedEndDate)) {
                Toast.makeText(this, "End date must be after the start date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update vacation object and database
            Vacation updatedVacation = new Vacation(vacationId, updatedTitle, updatedHotel, updatedStartDate, updatedEndDate);
            repository.updateVacation(updatedVacation);

            // Schedule vacation notifications
            scheduleNotifications(updatedVacation);

            Toast.makeText(this, "Vacation updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        });

        // Share button functionality: Share vacation details
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

        // Add Excursion button functionality: Open Add Excursion dialog
        addExcursionButton.setOnClickListener(v -> showAddExcursionDialog());

        // Back button functionality: Close this screen
        backButton.setOnClickListener(v -> finish());
    }

    // Handle actions for individual excursions
    private void handleExcursionAction(Excursion excursion, String action) {
        switch (action) {
            case "DELETE":
                repository.deleteExcursion(excursion);
                Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
                break;
            case "EDIT":
                showEditExcursionDialog(excursion);
                break;
        }
    }

    // Show a dialog to add a new excursion
    private void showAddExcursionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Add Excursion");
        builder.setView(inflater.inflate(R.layout.dialog_add_excursion, null))
                .setPositiveButton("Add", (dialog, id) -> {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    EditText titleInput = alertDialog.findViewById(R.id.excursion_title_input);
                    EditText dateInput = alertDialog.findViewById(R.id.excursion_date_input);

                    if (titleInput != null && dateInput != null) {
                        String title = titleInput.getText().toString().trim();
                        String date = dateInput.getText().toString().trim();

                        if (title.isEmpty() || date.isEmpty()) {
                            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!isValidDate(date)) {
                            Toast.makeText(this, "Invalid date format. Use MM/dd/yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Excursion newExcursion = new Excursion(0, title, date, vacationId);
                        repository.insertExcursion(newExcursion);
                        scheduleExcursionNotification(newExcursion);
                        Toast.makeText(this, "Excursion added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    // Schedule notifications for a vacation
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

    // Schedule notification for an excursion
    private void scheduleExcursionNotification(Excursion excursion) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date excursionDate = sdf.parse(excursion.getDate());
            if (excursionDate != null) {
                Intent intent = new Intent(this, ExcursionNotificationReceiver.class);
                intent.putExtra("title", excursion.getTitle());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        excursion.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, excursionDate.getTime(), pendingIntent);
                    Toast.makeText(this, "Alert set for " + excursion.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Failed to set alert. Invalid date format.", Toast.LENGTH_SHORT).show();
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

    private void showEditExcursionDialog(Excursion excursion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Edit Excursion");
        builder.setView(inflater.inflate(R.layout.dialog_edit_excursion, null))
                .setPositiveButton("Save", (dialog, id) -> {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    EditText titleInput = alertDialog.findViewById(R.id.excursion_edit_title_input);
                    EditText dateInput = alertDialog.findViewById(R.id.excursion_edit_date_input);

                    if (titleInput != null && dateInput != null) {
                        String updatedTitle = titleInput.getText().toString().trim();
                        String updatedDate = dateInput.getText().toString().trim();

                        if (updatedTitle.isEmpty() || updatedDate.isEmpty()) {
                            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!isValidDate(updatedDate)) {
                            Toast.makeText(this, "Invalid date format. Use MM/dd/yyyy.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        excursion.setTitle(updatedTitle);
                        excursion.setDate(updatedDate);
                        repository.updateExcursion(excursion);
                        Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .create()
                .show();

        // Pre-fill the fields with existing excursion details
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            EditText titleInput = alertDialog.findViewById(R.id.excursion_edit_title_input);
            EditText dateInput = alertDialog.findViewById(R.id.excursion_edit_date_input);
            if (titleInput != null) titleInput.setText(excursion.getTitle());
            if (dateInput != null) dateInput.setText(excursion.getDate());
        });
    }

}
