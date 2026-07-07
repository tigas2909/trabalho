package com.example.trabalho_loja_virtual.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public boolean logar(String email, String senha,
                        HttpServletResponse response,
                        HttpSession session) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        if (!userService.conferirSenha(senha, user.getSenha())) {
            return false;
        }

        //cookie
        Cookie ck = new Cookie("userId", String.valueOf(user.getId()));
        ck.setHttpOnly(true);
        ck.setPath("/");
        ck.setMaxAge(60 * 60 * 24);
        response.addCookie(ck);

        //sessão 
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("nome", user.getNome());

        return true;
    }

    public void logout(HttpServletResponse response) {
        Cookie ck = new Cookie("userId", null);
        ck.setMaxAge(0);
        ck.setPath("/");
        response.addCookie(ck);
    }
}