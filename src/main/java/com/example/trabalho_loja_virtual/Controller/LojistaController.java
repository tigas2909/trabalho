package com.example.trabalho_loja_virtual.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;

@Controller
@RequestMapping("/lojista")
public class LojistaController {
    @Autowired
    private UserService UserService;

    @GetMapping("/{email}")
    public String dashboard(@PathVariable String email, Model model) {
        User user = UserService.getUserByEmail(email);
        model.addAttribute("nome", user.getNome());
        return "/lojista/lojistaPagPrin";
    }
}
