package com.example.trabalho_loja_virtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trabalho_loja_virtual.entities.Item_Pedido;

public interface ItemPedidoRepository extends JpaRepository<Item_Pedido, Long> {
    @Query("SELECT i FROM Item_Pedido i WHERE i.pedido.id = :pedidoId")
    List<Item_Pedido> buscarItensDoPedido(@Param("pedidoId") Long pedidoId);
    
    
}
