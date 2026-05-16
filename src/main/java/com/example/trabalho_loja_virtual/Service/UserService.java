package com.example.trabalho_loja_virtual.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder PE;


    public String criptografarSenha(String senha) {
        String hsenha = PE.encode(senha);
        return hsenha; // Retorna a senha criptografada (substitua pela lógica real)
    }

    public boolean conferirSenha(String senhaDigitada, String senhaArmazenada) {
        return PE.matches(senhaDigitada, senhaArmazenada);
    }

    public boolean validarEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean validarSenha(String senha) {
        // Implementar lógica de validação de senha (ex: mínimo 8 caracteres, etc.)
        return senha.length() >= 8; // Exemplo simples de validação
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User insert(User user) {
        user.setSenha(criptografarSenha(user.getSenha()));
        return userRepository.save(user);
    }
}
