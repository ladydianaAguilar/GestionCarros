package com.miempresa.gestioncarros.Entidades;

public class Motivo {
    private String placa;
    private String fechamotivo;
    private String motivo;

    public Motivo() {
    }

    public Motivo(String placa, String fechamotivo, String motivo) {
        this.placa = placa;
        this.fechamotivo = fechamotivo;
        this.motivo = motivo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getFechamotivo() {
        return fechamotivo;
    }

    public void setFechamotivo(String fechamotivo) {
        this.fechamotivo = fechamotivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
