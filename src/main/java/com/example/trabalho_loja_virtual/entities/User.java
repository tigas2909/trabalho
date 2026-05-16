package com.example.trabalho_loja_virtual.entities;

import com.example.trabalho_loja_virtual.enums.Tipo_User;
import com.example.trabalho_loja_virtual.enums.Tipo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo_User tipoUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Tipo_User getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        if(tipoUser.equalsIgnoreCase("cliente")) {
            this.tipoUser = Tipo_User.CLIENTE;
        } else if(tipoUser.equalsIgnoreCase("lojista")) {
            this.tipoUser = Tipo_User.lOJISTA;
        } else {
            throw new IllegalArgumentException("Tipo de usuário inválido: " + tipoUser);
        }
    }

    public Tipo getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if(status.equalsIgnoreCase("ativo")) {
            this.status = Tipo.ATIVO;
        } else if(status.equalsIgnoreCase("inativo")) {
            this.status = Tipo.INATIVO;
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }
    
    
}