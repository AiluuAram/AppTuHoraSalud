package com.example.apptuhorasalud.infrastructure.entitys;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingresos_stock")
public class IngresoStockEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "medicamentoId")
    private int medicamentoId;
    @ColumnInfo(name = "medicamentoNombre")
    private String medicamentoNombre;
    @ColumnInfo(name = "cantidad")
    private int cantidad;
    @ColumnInfo(name = "fecha")
    private String fecha;
    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "isDeleted")
    private boolean isDeleted;

    public IngresoStockEntity(Long id, int medicamentoId, String medicamentoNombre, int cantidad, String fecha, int userId, boolean isDeleted) {
        this.id = id;
        this.medicamentoId = medicamentoId;
        this.medicamentoNombre = medicamentoNombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public IngresoStockEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(int medicamentoId) { this.medicamentoId = medicamentoId; }
    public String getMedicamentoNombre() { return medicamentoNombre; }
    public void setMedicamentoNombre(String medicamentoNombre) { this.medicamentoNombre = medicamentoNombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}