package com.miempresa.gestioncarros.Entidades;

public class Gastos {

    private String placa;
    private String fecha;
    private Double saldo;
    private String articulo;
    private Double costo;

    public Gastos() {
    }
    public Gastos(String placa, String fecha, Double saldo, String articulo, Double costo) {
        this.placa = placa;
        this.fecha = fecha;
        this.saldo = saldo;
        this.articulo = articulo;
        this.costo = costo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }
}
