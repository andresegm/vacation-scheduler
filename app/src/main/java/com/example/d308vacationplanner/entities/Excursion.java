package com.example.d308vacationplanner.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "excursions",
        foreignKeys = @ForeignKey(
                entity = Vacation.class,
                parentColumns = "id",
                childColumns = "vacationId",
                onDelete = ForeignKey.CASCADE // Automatically deletes excursions if the vacation is deleted
        )
)
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int id; // Primary Key

    private String title; // Excursion title
    private String date; // Excursion date in MM/dd/yy format
    private int vacationId; // Foreign key linking to Vacation

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }
}
