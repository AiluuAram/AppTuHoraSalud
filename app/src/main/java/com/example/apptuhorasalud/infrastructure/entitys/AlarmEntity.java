package com.example.apptuhorasalud.infrastructure.entitys;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class AlarmEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "medicineId")
    private int medicineId;
    @ColumnInfo(name = "medicineName")
    private String medicineName;
    @ColumnInfo(name = "dose")
    private String dose;
    @ColumnInfo(name = "hour")
    private int hour;
    @ColumnInfo(name = "minute")
    private int minute;
    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "isDeleted")
    private boolean isDeleted;
    @ColumnInfo(name = "isActive", defaultValue = "1")
    private boolean isActive;

    public AlarmEntity(Long id, int medicineId, String medicineName, String dose, int hour, int minute, int userId, boolean isDeleted, boolean isActive) {
        this.id = id;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.dose = dose;
        this.hour = hour;
        this.minute = minute;
        this.userId = userId;
        this.isDeleted = isDeleted;
        this.isActive = isActive;
    }

    public AlarmEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
