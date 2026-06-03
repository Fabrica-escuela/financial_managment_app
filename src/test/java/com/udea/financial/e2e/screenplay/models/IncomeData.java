package com.udea.financial.e2e.screenplay.models;

public class IncomeData {
    private Double valor;
    private String descripcion;
    private Integer categoriaId; // O String categoria si tu backend recibe el nombre directamente

    public IncomeData(Double valor, String descripcion, Integer categoriaId) {
        this.valor = valor;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
    }

    // Getters y Setters
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
}