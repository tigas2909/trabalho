package com.example.trabalho_loja_virtual.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.repository.ProdutosRepository;

@Service
public class ProdutoService {
    @Autowired
    private ProdutosRepository produtosRepository;

    public List<Produto> ItensBaixoDoEstoque() {
        return produtosRepository.findProdutosComEstoqueBaixo();
    }

    public List<Produto> searchByName(String nome) {
        return produtosRepository.findByNomeContaining(nome);
    }

    public List<Object[]> getProdutosMaisVendidos() {
        return produtosRepository.findProdutosMaisVendidos();
    }

    public Produto save(Produto produto) {
        return produtosRepository.save(produto);
    }

    public List<Produto> findAll() {
        return produtosRepository.findAll();
    }

    public void deleteById(Long id) {
        produtosRepository.deleteById(id);
    }
}
