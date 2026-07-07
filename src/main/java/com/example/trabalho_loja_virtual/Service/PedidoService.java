package com.example.trabalho_loja_virtual.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.Cliente;
import com.example.trabalho_loja_virtual.entities.Item_Pedido;
import com.example.trabalho_loja_virtual.entities.Pedidos;
import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.enums.PedidosStatus;
import com.example.trabalho_loja_virtual.repository.ClienteRepository;
import com.example.trabalho_loja_virtual.repository.ItemPedidoRepository;
import com.example.trabalho_loja_virtual.repository.PedidoRepository;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;

@Service
public class PedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // =========================
    // ITENS DO PEDIDO
    // =========================

    public boolean adicionarItemAoPedido(Long pedidoId, Long produtoId, int quantidade) {

        Pedidos pedido = pedidoRepository.findById(pedidoId).orElseThrow();
        Produto produto = produtosRepository.findById(produtoId).orElseThrow();

        if (produto.getEstoque() >= quantidade) {

            produto.setEstoque(produto.getEstoque() - quantidade);
            produtosRepository.save(produto);

            Item_Pedido itemPedido = new Item_Pedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setValorTotal(
                    produto.getPreco().multiply(BigDecimal.valueOf(quantidade))
            );

            itemPedidoRepository.save(itemPedido);

            pedido.setValor(
                    pedido.getValor().add(itemPedido.getValorTotal())
            );

            pedidoRepository.save(pedido);

            return true;
        }

        return false;
    }

    public boolean apagarItemDoPedido(Long itemPedidoId, long pedidoId) {

        Item_Pedido itemPedido = itemPedidoRepository.findById(itemPedidoId).orElseThrow();
        Produto produto = itemPedido.getProduto();

        produto.setEstoque(produto.getEstoque() + itemPedido.getQuantidade());
        produtosRepository.save(produto);

        Pedidos pedido = pedidoRepository.findById(pedidoId).orElseThrow();

        pedido.setValor(
                pedido.getValor().subtract(itemPedido.getValorTotal())
        );

        pedidoRepository.save(pedido);
        itemPedidoRepository.delete(itemPedido);

        return true;
    }

    // =========================
    // CRIAÇÃO DE PEDIDOS
    // =========================

    public Pedidos criarPedido(Cliente cliente) {

        Pedidos pedido = new Pedidos();
        pedido.setCliente(cliente);
        pedido.setValor(BigDecimal.ZERO);
        pedido.setStatus(PedidosStatus.ABERTO);

        pedidoRepository.save(pedido);

        return pedido;
    }

    public Long criarCarrinhoParaCliente(Long clienteId) {

        Cliente cliente = clienteRepository.findByUserId(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pedidos carrinho = new Pedidos();
        carrinho.setCliente(cliente);
        carrinho.setValor(BigDecimal.ZERO);
        carrinho.setStatus(PedidosStatus.ABERTO);

        pedidoRepository.save(carrinho);

        return carrinho.getId();
    }

    // =========================
    // CANCELAR / FINALIZAR
    // =========================

    public boolean cancelarPedido(Long clienteId) {

        Pedidos pedido = null;

        List<Pedidos> pedidosAbertos = pedidoRepository.findByCliente_Id(clienteId);

        for (Pedidos p : pedidosAbertos) {
            if (p.getStatus() == PedidosStatus.ABERTO) {
                pedido = p;
            }
        }

        if (pedido == null) return false;

        for (Item_Pedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtosRepository.save(produto);
        }

        pedido.setStatus(PedidosStatus.CANCELADO);
        pedidoRepository.save(pedido);

        return true;
    }

    public boolean finalizarPedido(Long clienteId) {

        Pedidos p = new Pedidos();

        List<Pedidos> pedidosAbertos = pedidoRepository.findByCliente_Id(clienteId);

        for (Pedidos pedido : pedidosAbertos) {
            if (pedido.getStatus() == PedidosStatus.ABERTO) {
                p = pedido;
            }
        }

        p.setStatus(PedidosStatus.CONCLUIDO);
        pedidoRepository.save(p);

        return true;
    }

    // =========================
    // BUSCAS
    // =========================

    public Optional<Pedidos> buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId);
    }

    public Optional<Pedidos> buscarPedidoAbertoPorClienteId(Long clienteId) {

        Optional<Pedidos> p = Optional.empty();

        List<Pedidos> pedidosAbertos = pedidoRepository.findByCliente_Id(clienteId);

        for (Pedidos pedido : pedidosAbertos) {
            if (pedido.getStatus() == PedidosStatus.ABERTO) {
                p = Optional.of(pedido);
            }
        }

        return p;
    }
}