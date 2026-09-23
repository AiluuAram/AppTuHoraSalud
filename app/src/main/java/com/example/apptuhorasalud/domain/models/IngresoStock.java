package com.example.apptuhorasalud.domain.models;

public class IngresoStock {
    private int id;
    private int medicamentoId;
    private String medicamentoNombre;
    private int cantidad;
    private String fecha;
    private int userId;
    private boolean isDeleted;

    public IngresoStock(int id, int medicamentoId, String medicamentoNombre, int cantidad, String fecha, int userId, boolean isDeleted) {
        this.id = id;
        this.medicamentoId = medicamentoId;
        this.medicamentoNombre = medicamentoNombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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