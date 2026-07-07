package com.example.trabalho_loja_virtual.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.Cliente;
import com.example.trabalho_loja_virtual.entities.Lojista;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.Tipo_User;
import com.example.trabalho_loja_virtual.repository.ClienteRepository;
import com.example.trabalho_loja_virtual.repository.LojistaRepository;
import com.example.trabalho_loja_virtual.repository.PedidoRepository;
import com.example.trabalho_loja_virtual.repository.UserRepository;

@Service
public class UserService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder PE;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LojistaRepository lojistaRepository;


    public UserService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // =========================
    // SENHA
    // =========================

    public String criptografarSenha(String senha) {
        return PE.encode(senha);
    }

    public boolean conferirSenha(String senhaDigitada, String senhaArmazenada) {
        return PE.matches(senhaDigitada, senhaArmazenada);
    }

    public boolean validarSenha(String senha) {
        return senha.length() >= 8;
    }

    // =========================
    // USUÁRIO
    // =========================

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> selectAll() {
        return userRepository.findAll();
    }

    public boolean validarEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User insert(User user) {
        user.setSenha(criptografarSenha(user.getSenha()));
        userRepository.save(user);
        salvarUserTipo(user);
        return user;
    }
    public User update(User user) {
        userRepository.save(user);
        return user;
    }
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // =========================
    // TIPO DE USUÁRIO
    // =========================

    public boolean salvarUserTipo(User u) {

    if (u.getTipoUser() == Tipo_User.lOJISTA) {

        if (!lojistaRepository.existsByUserId(u.getId())) {
            Lojista l = new Lojista();
            l.setUser(u);
            lojistaRepository.save(l);
        }

        return true;
    }

    if (u.getTipoUser() == Tipo_User.CLIENTE) {

        if (!clienteRepository.existsByUserId(u.getId())) {
            Cliente c = new Cliente();
            c.setUser(u);
            clienteRepository.save(c);
        }

        return true;
    }

    return false;
}
}