package com.example.apptuhorasalud.infrastructure.entitys;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class MedicineEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "quantity")
    private int quantity;
    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "isDeleted")
    private boolean isDeleted;

    public MedicineEntity(Long id, String name, int quantity, int userId, boolean isDeleted) {
        this.setId(id);
        this.setName(name);
        this.setQuantity(quantity);
        this.setUserId(userId);
        this.setDeleted(isDeleted);
    }

    public MedicineEntity () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    // Getters and setters
}
