package com.example.trabalho_loja_virtual.enums;

public enum Tipo {
    ATIVO("ativo"),
    INATIVO("inativo");

    private String status;
    Tipo(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
