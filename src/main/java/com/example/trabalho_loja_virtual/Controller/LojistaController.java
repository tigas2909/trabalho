package com.example.trabalho_loja_virtual.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;
import com.example.trabalho_loja_virtual.repository.UserRepository;

@Controller
@RequestMapping("/lojista")
public class LojistaController {
    @Autowired
    private UserService UserService;
    
    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private UserRepository UserRepository;

    @GetMapping("/{email}")
    public String dashboard(@PathVariable String email, Model model) {
        User user = UserService.getUserByEmail(email);
        model.addAttribute("nome", user.getNome());
        return "/lojista/lojistaPagPrin";
    }

    @GetMapping("/add")
    public String add() {
        return "lojista/userForm";
    }

    @PostMapping("/save")
    public String save(User user) {
        UserService.insert(user);
        return "redirect:/login/logout";
    }

    @GetMapping("/lojista/dashboard")
    public String dashboard(
        @CookieValue(value = "userId", required = false) Long userId,
        Model model) {

        // 🔴 valida login
        if (userId == null) {
            return "redirect:/login";
        }

        User user = UserRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // 1. Estoque baixo
        List<Produto> estoqueBaixo =
                produtosRepository.findProdutosComEstoqueBaixo();

        // 2. Mais vendidos
        List<Object[]> maisVendidos =
                produtosRepository.findProdutosMaisVendidos();

        // 3. Estoque total
        Integer estoqueTotal = produtosRepository
                .findAll()
                .stream()
                .mapToInt(Produto::getEstoque)
                .sum();

        // 👇 dados da tela
        model.addAttribute("nome", user.getNome());
        model.addAttribute("estoqueBaixo", estoqueBaixo);
        model.addAttribute("maisVendidos", maisVendidos);
        model.addAttribute("estoqueTotal", estoqueTotal);

        return "dashboard";
    }
}
