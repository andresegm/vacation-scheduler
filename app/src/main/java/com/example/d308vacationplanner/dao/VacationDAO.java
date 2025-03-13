package com.example.d308vacationplanner.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacationplanner.entities.Excursion;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {

    // Insert a new vacation
    @Insert
    void insertVacation(Vacation vacation);

    // Update an existing vacation
    @Update
    void updateVacation(Vacation vacation);

    // Delete a vacation
    @Delete
    void deleteVacation(Vacation vacation);

    // Insert a new excursion
    @Insert
    void insertExcursion(Excursion excursion);

    // Update an existing excursion
    @Update
    void updateExcursion(Excursion excursion);

    // Delete an excursion
    @Delete
    void deleteExcursion(Excursion excursion);

    // Retrieve all vacations
    @Query("SELECT * FROM vacations ORDER BY startDate ASC")
    LiveData<List<Vacation>> getAllVacations();

    // Count excursions for a specific vacation
    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int countExcursionsForVacation(int vacationId);

    // Retrieve all excursions for a specific vacation
    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId ORDER BY date ASC")
    LiveData<List<Excursion>> getExcursionsForVacation(int vacationId);
}
