package com.example.trabalho_loja_virtual.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.repository.ClienteRepository;

import com.example.trabalho_loja_virtual.entities.Cliente;
import com.example.trabalho_loja_virtual.entities.Pedidos;
import com.example.trabalho_loja_virtual.enums.PedidosStatus;

@Service
public class ClienteService {
    @Autowired
        private ClienteRepository clienteRepository;

    @Autowired
        private PedidoService pedidoService;
    
    public Pedidos criarPedido(Long clienteId) {
        Cliente cliente = clienteRepository.findByUserId(clienteId).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return pedidoService.criarPedido(cliente);
    }

    public boolean cancelarPedido(Long clienteId) {
        return pedidoService.cancelarPedido(clienteId);
    }

    public boolean finalizarPedido(Long clienteId) {
        return pedidoService.finalizarPedido(clienteId);
    }

    public boolean adicionarItemAoPedido(Long pedidoId, Long produtoId, int quantidade, Long clienteId) {
        Pedidos p = pedidoService.buscarPedidoPorId(pedidoId).orElseThrow();
        if(p == null || !p.getStatus().equals(PedidosStatus.ABERTO)) {
            this.criarPedido(clienteId);
            p = pedidoService.buscarPedidoPorId(pedidoId).orElseThrow();
        }
        return pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade);

    }

}
