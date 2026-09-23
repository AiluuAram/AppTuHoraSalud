package com.example.apptuhorasalud.domain.models;

public class Alarm {
    private int id;
    private int medicineId;
    private String medicineName;
    private String dose;
    private int hour;
    private int minute;
    private int userId;
    private boolean isDeleted;
    private boolean isActive;

    public Alarm(int id, int medicineId, String medicineName, String dose, int hour, int minute, int userId, boolean isDeleted, boolean isActive) {
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

    public Alarm(int id, int medicineId, String medicineName, String dose, int hour, int minute, int userId, boolean isDeleted) {
        this(id, medicineId, medicineName, dose, hour, minute, userId, isDeleted, true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
