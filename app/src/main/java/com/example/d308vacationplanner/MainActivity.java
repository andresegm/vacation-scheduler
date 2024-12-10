package com.example.d308vacationplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.activities.VacationDetailsActivity;
import com.example.d308vacationplanner.adapters.VacationAdapter;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind RecyclerView
        RecyclerView vacationList = findViewById(R.id.vacation_list);
        vacationList.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        List<Vacation> sampleVacations = new ArrayList<>();
        sampleVacations.add(new Vacation("Spring Break", "Grand Beach Resort", "03/20/2024", "03/27/2024"));
        sampleVacations.add(new Vacation("Summer Vacation", "Sunny Paradise Hotel", "07/01/2024", "07/15/2024"));

        // Set up the adapter
        VacationAdapter adapter = new VacationAdapter(sampleVacations, vacation -> {
            // Navigate to VacationDetailsActivity on item click
            Intent intent = new Intent(MainActivity.this, VacationDetailsActivity.class);
            intent.putExtra("title", vacation.getTitle());
            intent.putExtra("hotel", vacation.getHotel());
            intent.putExtra("startDate", vacation.getStartDate());
            intent.putExtra("endDate", vacation.getEndDate());
            startActivity(intent);
        });
        vacationList.setAdapter(adapter);
    }
}
