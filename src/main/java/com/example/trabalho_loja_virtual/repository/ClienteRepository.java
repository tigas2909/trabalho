package com.example.trabalho_loja_virtual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trabalho_loja_virtual.entities.Cliente;

public interface ClienteRepository extends JpaRepository< Cliente , Long>{
    Optional<Cliente> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
