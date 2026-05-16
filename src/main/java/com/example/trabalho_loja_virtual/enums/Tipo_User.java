package com.example.trabalho_loja_virtual.enums;

public enum Tipo_User {
    lOJISTA("lojista"),
    CLIENTE("cliente");

    private String tipo;
    Tipo_User(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
