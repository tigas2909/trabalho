package com.example.trabalho_loja_virtual.Controller;

import com.example.trabalho_loja_virtual.repository.CategoriaRepository;
import com.example.trabalho_loja_virtual.repository.ItemPedidoRepository;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.trabalho_loja_virtual.Service.PedidoService;
import com.example.trabalho_loja_virtual.Service.ProdutoService;
import com.example.trabalho_loja_virtual.Service.UserService;
import com.example.trabalho_loja_virtual.entities.Cliente;
import com.example.trabalho_loja_virtual.entities.Item_Pedido;
import com.example.trabalho_loja_virtual.entities.Pedidos;
import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.entities.User;

import java.math.BigDecimal;
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

    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

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

        Map<Long, Integer> carrinhoMap = getOrCreateCarrinho(session);

        List<Item_Pedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : carrinhoMap.entrySet()) {
            Long produtoId = entry.getKey();
            Integer quantidade = entry.getValue();
            if (quantidade != null && quantidade > 0) {
                Produto produto = produtosRepository.findById(produtoId).orElse(null);
                if (produto != null) {
                    Item_Pedido item = new Item_Pedido();
                    item.setProduto(produto);
                    item.setQuantidade(quantidade);
                    BigDecimal valorItem = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
                    item.setValorTotal(valorItem);
                    itens.add(item);
                    total = total.add(valorItem);
                }
            }
        }

        Pedidos carrinhoView = new Pedidos();
        carrinhoView.setValor(total);
        // id remains null since not yet persisted

        model.addAttribute("carrinho", carrinhoView);
        model.addAttribute("itens", itens);

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

        if (quantidade == null || quantidade < 1) quantidade = 1;

        Map<Long, Integer> carrinho = getOrCreateCarrinho(session);

        Produto produto = produtosRepository.findById(produtoId).orElse(null);
        if (produto == null) {
            return "redirect:/cliente/produtos";
        }

        int atual = carrinho.getOrDefault(produtoId, 0);
        int novo = atual + quantidade;

        // Verifica estoque no momento da adição
        if (novo > produto.getEstoque()) {
            // não adiciona mais que o disponível (pode ajustar para mensagem de erro se quiser)
            novo = produto.getEstoque();
        }

        if (novo > 0) {
            carrinho.put(produtoId, novo);
        } else {
            carrinho.remove(produtoId);
        }

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

    @PostMapping("/pedido/finalizar")
    public String finalizarPedidos(HttpServletRequest request, HttpSession session) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        Map<Long, Integer> carrinhoMap = getOrCreateCarrinho(session);
        if (carrinhoMap.isEmpty()) {
            return "redirect:/cliente/carrinho";
        }

        // Cria o pedido somente agora, com status Aberto
        Pedidos pedido = new Pedidos();
        pedido.setCliente(cliente);
        pedido.setStatus(PedidosStatus.ABERTO);
        pedido.setValor(BigDecimal.ZERO);
        pedidoRepository.save(pedido);  // salva primeiro para ter ID

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : carrinhoMap.entrySet()) {
            Long produtoId = entry.getKey();
            Integer quantidade = entry.getValue();
            if (quantidade == null || quantidade <= 0) continue;

            Produto produto = produtosRepository.findById(produtoId).orElse(null);
            if (produto != null && produto.getEstoque() >= quantidade) {
                // Deduz estoque só no momento do registro do pedido
                produto.setEstoque(produto.getEstoque() - quantidade);
                produtosRepository.save(produto);

                Item_Pedido item = new Item_Pedido();
                item.setPedido(pedido);
                item.setProduto(produto);
                item.setQuantidade(quantidade);
                BigDecimal valorItem = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
                item.setValorTotal(valorItem);
                itemPedidoRepository.save(item);

                total = total.add(valorItem);
            }
        }

        pedido.setValor(total);
        pedidoRepository.save(pedido);

        // Limpa o carrinho da sessão
        session.removeAttribute("carrinho");

        return "redirect:/cliente/historico";
    }

    @PostMapping("/pedido/cancelar")
    public String cancelarPedidos(HttpServletRequest request, HttpSession session) {

        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        // Apenas limpa o carrinho da sessão (sem Pedido persistido ainda)
        session.removeAttribute("carrinho");

        return "redirect:/cliente/historico";
    }

    @PostMapping("/pedido/{id}/cancelar")
    public String cancelarPedidoEspecifico(@PathVariable Long id, HttpServletRequest request, HttpSession session) {
        Long userId = getUserId(request, session);
        if (userId == null) return "redirect:/login/";

        Cliente cliente = clienteRepository.findByUserId(userId)
                .orElseGet(() -> criarClienteParaUsuario(userId));

        Pedidos pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null && pedido.getCliente() != null 
                && pedido.getCliente().getId().equals(cliente.getId()) 
                && pedido.getStatus() == PedidosStatus.ABERTO) {

            for (Item_Pedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                produtosRepository.save(produto);
            }

            pedido.setStatus(PedidosStatus.CANCELADO);
            pedidoRepository.save(pedido);
        }

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

    // =========================
    // CARRINHO TEMPORÁRIO (SESSION) - não persiste Pedido até finalizar
    // =========================

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getOrCreateCarrinho(HttpSession session) {
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new HashMap<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }
}