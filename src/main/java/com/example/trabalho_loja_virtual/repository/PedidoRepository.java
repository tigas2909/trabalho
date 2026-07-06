package com.example.trabalho_loja_virtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trabalho_loja_virtual.entities.Pedidos;

public interface PedidoRepository extends JpaRepository<Pedidos , Long>{
    @Query("select p from Pedidos p join fetch p.cliente c join fetch c.user u where c.id = :clienteId")
    List<Pedidos> findByCliente_Id(@Param("clienteId") Long clienteId);
}