package com.example.trabalho_loja_virtual.repository;

import com.example.trabalho_loja_virtual.entities.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
}