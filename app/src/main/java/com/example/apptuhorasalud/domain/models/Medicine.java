package com.example.apptuhorasalud.domain.models;

public class Medicine {
    private int id;
    private String name;
    private int quantity;
    private int userId;
    private boolean isDeleted;

    public Medicine(int id, String name, int quantity, int userId, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
}
