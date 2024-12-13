package com.example.d308vacationplanner.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.d308vacationplanner.dao.VacationDAO;
import com.example.d308vacationplanner.entities.Vacation;
import com.example.d308vacationplanner.entities.Excursion;

import java.util.concurrent.Executors;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {

    public abstract VacationDAO vacationDAO();

    // Singleton instance
    private static volatile VacationDatabase INSTANCE;

    public static VacationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabase.class) {
                if (INSTANCE == null) {
                    context.deleteDatabase("vacation_database"); // Force recreation by deleting the old database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    VacationDatabase.class, "vacation_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        VacationDAO dao = INSTANCE.vacationDAO();

                                        // Insert sample vacations
                                        dao.insertVacation(new Vacation(0, "Spring Break", "Grand Beach Resort", "03/20/2024", "03/27/2024"));
                                        dao.insertVacation(new Vacation(0, "Summer Vacation", "Sunny Paradise Hotel", "07/01/2024", "07/15/2024"));

                                        // Insert sample excursions
                                        dao.insertExcursion(new Excursion(0, "Snorkeling", "03/21/2024", 1)); // Associated with Spring Break
                                        dao.insertExcursion(new Excursion(0, "Hiking", "03/23/2024", 1));     // Associated with Spring Break
                                        dao.insertExcursion(new Excursion(0, "City Tour", "07/05/2024", 2));  // Associated with Summer Vacation
                                    });
                                }

                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}