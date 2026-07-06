package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.repository.CategoriaRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.trabalho_loja_virtual.Service.PedidoService;
import com.example.trabalho_loja_virtual.Service.ProdutoService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.Cliente;
import com.example.trabalho_loja_virtual.entities.Pedidos;
import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.entities.User;
import com.example.trabalho_loja_virtual.enums.PedidosStatus;
import com.example.trabalho_loja_virtual.repository.ClienteRepository;
import com.example.trabalho_loja_virtual.repository.PedidoRepository;
import com.example.trabalho_loja_virtual.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    private UserService US;

    @Autowired
    private ProdutoService PS;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    public ClienteController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =========================
    // CLIENTE / INICIAL
    // =========================

    @GetMapping("/{email:.+}")
    public String paginaInicial(@PathVariable String email, Model model) {
        User user = US.getUserByEmail(email);
        model.addAttribute("nome", user.getNome());
        return "user/paginaInicial";
    }

    @GetMapping("/retornar")
    public String retornar(HttpServletRequest request, HttpSession session) {
        Long userId = getUserId(request, session);

        if (userId == null) {
            return "redirect:/login/";
        }

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return "redirect:/cliente/" + u.getEmail();
    }

    // =========================
    // PRODUTOS
    // =========================

    @GetMapping("/produtos")
    public String produtosSelect(Model model) {
        List<Produto> produtos = PS.listagemCliente();
        model.addAttribute("produtos", produtos);
        return "user/produtos";
    }

    @GetMapping("/produto/{id}")
    public String produto(@PathVariable Long id, Model model) {
        Produto produto = PS.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "user/produto-detalhe";
    }

    // =========================
    // CARRINHO
    // =========================

    @GetMapping("/carrinho")
    public String carrinho(HttpServletRequest request, HttpSession session, Model model) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        Pedidos carrinho = pedidoService
                .buscarPedidoAbertoPorClienteId(cliente.getId())
                .orElseGet(() -> pedidoService.criarPedido(cliente));

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("itens", carrinho.getItens());

        return "user/carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarCarrinho(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            HttpServletRequest request,
            HttpSession session) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        Pedidos carrinho = pedidoService
                .buscarPedidoAbertoPorClienteId(cliente.getId())
                .orElseGet(() -> pedidoService.criarPedido(cliente));

        pedidoService.adicionarItemAoPedido(carrinho.getId(), produtoId, quantidade);

        return "redirect:/cliente/produtos";
    }

    // =========================
    // PEDIDOS / HISTÓRICO
    // =========================

    @GetMapping("/historico")
    public String pedidosHistorico(HttpServletRequest request, HttpSession session, Model model) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        List<Pedidos> pedidos = pedidoRepository.findByCliente_Id(cliente.getId());

        List<Pedidos> pedidosAberto = new ArrayList<>();
        List<Pedidos> pedidosFechados = new ArrayList<>();
        List<Pedidos> pedidosCancelados = new ArrayList<>();

        for (Pedidos p : pedidos) {
            if (p.getStatus() == PedidosStatus.ABERTO) {
                pedidosAberto.add(p);
            } else if (p.getStatus() == PedidosStatus.CANCELADO) {
                pedidosCancelados.add(p);
            } else if (p.getStatus() == PedidosStatus.CONCLUIDO) {
                pedidosFechados.add(p);
            }
        }

        model.addAttribute("abertos", pedidosAberto);
        model.addAttribute("concluidos", pedidosFechados);
        model.addAttribute("cancelados", pedidosCancelados);
        model.addAttribute("user", user);

        return "user/historico";
    }

    @GetMapping("/pedido/finalizar")
    public String finalizarPedidos(HttpServletRequest request, HttpSession session) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        boolean p = pedidoService.finalizarPedido(cliente.getId());

        if (!p) return "redirect:/cliente/retornar";

        return "redirect:/cliente/historico";
    }

    @GetMapping("/pedido/cancelar")
    public String cancelarPedidos(HttpServletRequest request, HttpSession session) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        boolean p = pedidoService.cancelarPedido(cliente.getId());

        if (!p) return "redirect:/cliente/retornar";

        return "redirect:/cliente/historico";
    }

    // =========================
    // HELPERS
    // =========================

    private Cliente criarClienteParaUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Cliente cliente = new Cliente();
        cliente.setUser(user);
        return clienteRepository.save(cliente);
    }

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