package com.miempresa.gestioncarros;

public class Placa {
    private Integer idplaca;
    private String placa;

    public Placa() {
    }

    public Placa(Integer idplaca, String placa) {
        this.idplaca = idplaca;
        this.placa = placa;
    }

    public Integer getIdplaca() {
        return idplaca;
    }

    public void setIdplaca(Integer idplaca) {
        this.idplaca = idplaca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    @Override
    public String toString(){
        return placa;
    }

}
