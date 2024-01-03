package com.miempresa.gestioncarros.Entidades;

public class Ingresos {
    private String placa;
    private String fechaingreso;
    private double monto;

    public Ingresos() {
    }

    public Ingresos(String placa, String fechaingreso, double monto) {
        this.placa = placa;
        this.fechaingreso = fechaingreso;
        this.monto = monto;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(String fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
