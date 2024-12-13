package com.example.d308vacationplanner.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.d308vacationplanner.dao.VacationDAO;
import com.example.d308vacationplanner.entities.Excursion;
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
        Log.d("VacationRepository", "Updating vacation: ID=" + vacation.getId() + ", Title=" + vacation.getTitle());
        executorService.execute(() -> vacationDAO.updateVacation(vacation));
    }


    // Delete a vacation with validation
    public void deleteVacation(Vacation vacation, DeletionCallback callback) {
        executorService.execute(() -> {
            int excursionCount = vacationDAO.countExcursionsForVacation(vacation.getId());
            if (excursionCount > 0) {
                callback.onFailure("Cannot delete vacation with associated excursions.");
            } else {
                vacationDAO.deleteVacation(vacation);
                callback.onSuccess();
            }
        });
    }

    public interface DeletionCallback {
        void onSuccess();
        void onFailure(String message);
    }


    // Fetch all vacations
    public LiveData<List<Vacation>> getAllVacations() {
        return vacationDAO.getAllVacations();
    }

    public LiveData<List<Excursion>> getExcursionsForVacation(int vacationId) {
        return vacationDAO.getExcursionsForVacation(vacationId);
    }

    public void insertExcursion(Excursion excursion) {
        executorService.execute(() -> vacationDAO.insertExcursion(excursion));
    }

}
