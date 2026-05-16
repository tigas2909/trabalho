package com.example.trabalho_loja_virtual.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Item_Pedido")
public class Item_Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedidos pedido;

    private int quantidade;
    private double valorTotal;


    public Long getId() { return id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Pedidos getPedido() { return pedido; }
    public void setPedido(Pedidos pedido) { this.pedido = pedido; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}