package com.example.d308vacationplanner.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308vacationplanner.dao.VacationDAO;
import com.example.d308vacationplanner.entities.Vacation;
import com.example.d308vacationplanner.entities.Excursion;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {

    public abstract VacationDAO vacationDAO();

    // Singleton instance
    private static volatile VacationDatabase INSTANCE;

    public static VacationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    VacationDatabase.class, "vacation_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
