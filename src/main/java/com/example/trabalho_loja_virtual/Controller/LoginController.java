package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.Service.LoginService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.Tipo_User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String login() {
        return "login/loginForm";
    }

    @PostMapping("/login/enter")
    public String login(String email, String senha,
                        HttpServletResponse response,
                        HttpSession session) {

        boolean ok = loginService.logar(email, senha, response, session);
        User user = userService.getUserByEmail(email);
        if (ok) {
            if (user.getTipoUser() == Tipo_User.lOJISTA) {
                return "redirect:/lojista/" + email;
            } else {
                return "redirect:/cliente/dashboard";
            }
        } else {
            return "redirect:/login/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        loginService.logout(response);
        return "redirect:/login/";
    }
}