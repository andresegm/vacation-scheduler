package com.example.d308vacationplanner.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.d308vacationplanner.dao.VacationDAO;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationRepository {

    private final VacationDAO vacationDAO;
    private final ExecutorService executorService;

    public VacationRepository(Context context) {
        VacationDatabase database = VacationDatabase.getDatabase(context);
        vacationDAO = database.vacationDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert a vacation
    public void insertVacation(Vacation vacation) {
        executorService.execute(() -> vacationDAO.insertVacation(vacation));
    }

    // Update a vacation
    public void updateVacation(Vacation vacation) {
        executorService.execute(() -> vacationDAO.updateVacation(vacation));
    }

    // Delete a vacation
    public void deleteVacation(Vacation vacation) {
        executorService.execute(() -> vacationDAO.deleteVacation(vacation));
    }

    // Fetch all vacations
    public LiveData<List<Vacation>> getAllVacations() {
        return vacationDAO.getAllVacations();
    }
}
