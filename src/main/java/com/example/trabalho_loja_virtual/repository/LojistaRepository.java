package com.example.trabalho_loja_virtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trabalho_loja_virtual.entities.Lojista;

public interface LojistaRepository extends JpaRepository< Lojista , Long>{
    boolean existsByUserId(Long userId);
}
