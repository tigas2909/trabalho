package com.example.trabalho_loja_virtual.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.User;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/add")
    public String add() {
        return "user/userForm";
    }

    @PostMapping("/save")
    public String save(User user) {
        userService.insert(user);
        return "redirect:/lojista/dashboard";
    }
}
