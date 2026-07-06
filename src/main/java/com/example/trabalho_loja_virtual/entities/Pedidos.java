package com.example.trabalho_loja_virtual.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.trabalho_loja_virtual.enums.PedidosStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pedidos")
public class Pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<Item_Pedido> itens = new ArrayList<>();
    
    @NotNull(message = "O valor do pedido é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O valor não pode ser negativo")
    private BigDecimal valor;

    @NotNull(message = "O status do pedido é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "pedido_status", nullable = false)
    private PedidosStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<Item_Pedido> getItens() {
        return itens;
    }

    public void setItens(List<Item_Pedido> itens) {
        this.itens = itens;
    }

    public PedidosStatus getStatus() {
        return status;
    }

    public void setStatus(PedidosStatus status) {
        this.status = status;
    }

}
