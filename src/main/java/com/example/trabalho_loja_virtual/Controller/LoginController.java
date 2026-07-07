package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.Service.LoginService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.Tipo_User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/", "", "/login"})
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    // =========================
    // LOGIN
    // =========================

    @GetMapping({"", "/"})
    public String login() {
        return "login/loginForm";
    }

    @PostMapping("/enter")
    public String login(String email, String senha,
                        HttpServletResponse response,
                        HttpSession session) {

        boolean ok = loginService.logar(email, senha, response, session);
        User user = userService.getUserByEmail(email);

        if (ok) {
            if (user.getTipoUser() == Tipo_User.lOJISTA) {
                return "redirect:/lojista/" + email;
            } else {
                return "redirect:/cliente/" + email;
            }
        }

        return "redirect:/login/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        loginService.logout(response);
        return "redirect:/login/";
    }

    // =========================
    // USUÁRIO
    // =========================

    @GetMapping("/add")
    public String novoUsuario(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "login/userForm";
    }

    @PostMapping("/save")
    public String save(@Valid User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "login/userForm";
        }

        if (userService.validarEmail(user.getEmail())) {
            result.rejectValue("email", null, "Este email já está cadastrado");
            model.addAttribute("user", user);
            return "login/userForm";
        }

        try {
            userService.insert(user);
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça o login.");
            return "redirect:/login/";
        } catch (Exception e) {
            // Captura erros inesperados (ex: falha no banco)
            result.reject("error.global", "Ocorreu um erro inesperado ao cadastrar. Tente novamente.");
            model.addAttribute("user", user);
            return "login/userForm";
        }
    }
}