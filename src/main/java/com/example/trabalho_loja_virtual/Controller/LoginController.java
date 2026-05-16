package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.Service.LoginService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.Tipo_User;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/enter")
    public String enter(@RequestParam String email, 
                        @RequestParam String senha, 
                        HttpServletResponse response) {

        boolean isAuthenticated = loginService.logar(email, senha, response);

        if (isAuthenticated) {
            User user = userService.getUserByEmail(email);
            if (user.getTipoUser() == Tipo_User.lOJISTA) {
                return "redirect:/lojista/"+user.getEmail();
            } else {
                return "/cliente/dashboard";
            }
        } else {
            return "redirect:/login/?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        loginService.logout(response);
        return "redirect:/login/";
    }
}