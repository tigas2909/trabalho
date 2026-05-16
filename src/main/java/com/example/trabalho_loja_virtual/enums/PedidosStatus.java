package com.example.trabalho_loja_virtual.enums;

public enum PedidosStatus {
    CONCLUIDO("concluido"),
    PENDENTE("pendente"),
    CANCELADO("cancelado");

    private String status;
    PedidosStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
