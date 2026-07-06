package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.Service.PedidoService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.Categoria;
import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.Tipo;
import com.example.trabalho_loja_virtual.repository.CategoriaRepository;
import com.example.trabalho_loja_virtual.repository.ItemPedidoRepository;
import com.example.trabalho_loja_virtual.repository.PedidoRepository;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;
import com.example.trabalho_loja_virtual.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/lojista")
public class LojistaController {

    @Autowired
    private UserService UserService;

    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private UserRepository UserRepository;

    @Autowired
    private PedidoRepository PedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // =========================
    // DASHBOARD / HOME
    // =========================

    @GetMapping("/{email}")
    public String dashboard(@PathVariable String email, Model model) {
        System.out.println("EMAIL RECEBIDO: " + email);

        User user = UserService.getUserByEmail(email);

        System.out.println("USER: " + user);

        model.addAttribute("nome", user.getNome());
        return "/lojista/lojistaPagPrin";
    }

    @GetMapping("/dashboard")
    public String dashboard(@CookieValue(value = "userId", required = false) Long userId,
                            Model model) {

        if (userId == null) return "redirect:/login";

        User user = UserRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        List<Produto> estoqueBaixo =
                produtosRepository.findProdutosComEstoqueBaixo();

        List<Object[]> maisVendidos =
                produtosRepository.findProdutosMaisVendidos();

        Integer estoqueTotal =
                produtosRepository.findAll()
                        .stream()
                        .mapToInt(Produto::getEstoque)
                        .sum();

        model.addAttribute("nome", user.getNome());
        model.addAttribute("estoqueBaixo", estoqueBaixo);
        model.addAttribute("maisVendidos", maisVendidos);
        model.addAttribute("estoqueTotal", estoqueTotal);

        return "lojista/estoque";
    }

    @GetMapping("/retornar")
    public String retornar(HttpServletRequest request, HttpSession session) {

        Long userId = getUserId(request, session);

        if (userId == null) {
            return "redirect:/login/";
        }

        User u = UserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return "redirect:/lojista/" + u.getEmail();
    }

    // =========================
    // USUÁRIO
    // =========================

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "lojista/userForm";
    }

    @PostMapping("/save")
    public String save(User user) {
        UserService.insert(user);
        return "redirect:/lojista/retornar";
    }
    @GetMapping("/listarAll")
    public String userList(Model model) {
        List<User> users = UserService.selectAll();

        model.addAttribute("users", users);
        
        return "lojista/ListaUser";
    }
    @GetMapping("/editarUser/{id}")
    public String userUpdate(@PathVariable Long id, Model model){

        User user = UserService.findById(id);

        model.addAttribute("user", user);
        

        return "lojista/userForm";
    }

    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id) {
        User user = UserRepository.findById(id).orElseThrow();
        user.setStatus(user.getStatus() == Tipo.ATIVO ? Tipo.INATIVO.getStatus() : Tipo.ATIVO.getStatus());
        UserRepository.save(user);
        return "redirect:/lojista/listarAll";
    }

    // =========================
    // ESTOQUE / PRODUTOS
    // =========================

    @GetMapping("/estoque")
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtosRepository.findAll());
        return "lojista/dashboard";
    }

    @PostMapping("/trocar-status/{id}")
    public String trocarStatus(@PathVariable Long id) {

        Produto produto = produtosRepository.findById(id).orElseThrow();

        if (produto.getStatus() == Tipo.ATIVO) {
            produto.setStatus(Tipo.INATIVO);
        } else {
            produto.setStatus(Tipo.ATIVO);
        }

        produtosRepository.save(produto);

        return "redirect:/lojista/estoque";
    }

    @GetMapping("/produto/editar/{id}")
    public String editarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtosRepository.findById(id).orElseThrow();
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "lojista/produtoForm";
    }
    @GetMapping("/produto/add")
    public String addProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "lojista/produtoForm";
    }

    @PostMapping("/produto/save")
    public String salvarProduto(Produto produto) {
        produtosRepository.save(produto);
        return "redirect:/lojista/estoque";
    }

    @GetMapping("/categoria/add")
public String addCategoria(Model model) {
    model.addAttribute("categoria", new Categoria());
    return "lojista/categoriaForm";
}
    @PostMapping("/categoria/save")
    public String salvarCategoria(Categoria categoria) {
        categoriaRepository.save(categoria);
        return "redirect:/lojista/estoque";
    }

    // =========================
    // PEDIDOS
    // =========================

    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {

        model.addAttribute("pedidos", PedidoRepository.findAll());

        return "lojista/pedidos";
    }

    @GetMapping("/pedidos/{id}/editar")
    public String editarPedido(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", PedidoRepository.findById(id).orElseThrow());
        model.addAttribute("produtos", produtosRepository.findAll());
        return "lojista/editarPedido";
    }

    @PostMapping("/pedidos/{id}/adicionar-item")
    public String adicionarItem(@PathVariable Long id,
                                @RequestParam Long produtoId,
                                @RequestParam int quantidade,
                                Model model) {

        boolean result = pedidoService.adicionarItemAoPedido(id, produtoId, quantidade);

        if (!result) {
            System.out.println("Erro ao adicionar item ao pedido");
        }

        return "redirect:/lojista/pedidos/" + id + "/editar";
    }

    @PostMapping("/pedidos/{id}/remover-item/{itemId}")
    public String removerItem(@PathVariable Long id, @PathVariable Long itemId) {

        boolean result = pedidoService.apagarItemDoPedido(itemId, id);

        if (!result) {
            System.out.println("Erro ao remover item do pedido");
        }

        return "redirect:/lojista/pedidos/" + id + "/editar";
    }

    // =========================
    // HELPERS
    // =========================

    private Long getUserId(HttpServletRequest request, HttpSession session) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    try {
                        return Long.valueOf(cookie.getValue());
                    } catch (Exception ignored) {}
                }
            }
        }

        if (session != null) {
            Object sessionUserId = session.getAttribute("userId");

            if (sessionUserId instanceof Long) return (Long) sessionUserId;
            if (sessionUserId instanceof Integer) return ((Integer) sessionUserId).longValue();

            if (sessionUserId instanceof String) {
                try {
                    return Long.valueOf((String) sessionUserId);
                } catch (Exception ignored) {}
            }
        }

        return null;
    }
}