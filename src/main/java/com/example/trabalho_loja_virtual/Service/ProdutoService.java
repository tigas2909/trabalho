package com.example.trabalho_loja_virtual.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.enums.Tipo;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;
import com.example.trabalho_loja_virtual.repository.PedidoRepository;
import com.example.trabalho_loja_virtual.repository.UserRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // CONSULTAS / LISTAGENS
    // =========================

    public List<Produto> findAll() {
        return produtosRepository.findAll();
    }

    public List<Produto> listagemCliente() {
        return produtosRepository.findByStatus(Tipo.ATIVO);
    }

    public List<Produto> itensBaixoDoEstoque() {
        return produtosRepository.findProdutosComEstoqueBaixo();
    }

    public List<Produto> searchByName(String nome) {
        return produtosRepository.findByNomeContaining(nome);
    }

    public List<Object[]> getProdutosMaisVendidos() {
        return produtosRepository.findProdutosMaisVendidos();
    }

    // =========================
    // CRUD
    // =========================

    public Produto save(Produto produto) {
        return produtosRepository.save(produto);
    }

    public void deleteById(Long id) {
        produtosRepository.deleteById(id);
    }

    // =========================
    // REGRAS DE NEGÓCIO
    // =========================

    public Produto buscarPorId(Long id) {

        Produto produto = produtosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getStatus() != Tipo.ATIVO) {
            throw new RuntimeException("Produto indisponível");
        }

        return produto;
    }
}