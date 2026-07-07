package com.example.trabalho_loja_virtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trabalho_loja_virtual.entities.Categoria;

public interface CategoriaRepository extends JpaRepository< Categoria,Long> {

}
