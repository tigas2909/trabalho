package com.example.trabalho_loja_virtual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trabalho_loja_virtual.entities.Produto;
import com.example.trabalho_loja_virtual.enums.Tipo;

public interface ProdutosRepository extends JpaRepository<Produto, Long> {

    // =========================
    // ESTOQUE
    // =========================

    @Query(value = "SELECT p FROM Produto p WHERE p.estoque < p.qtdMinima")
    List<Produto> findProdutosComEstoqueBaixo();

    // =========================
    // BUSCA
    // =========================

    @Query("SELECT p FROM Produto p WHERE p.nome LIKE %:nome%")
    List<Produto> findByNomeContaining(@Param("nome") String nome);

    @Query(value = """
        SELECT p.id,
               p.nome,
               p.preco,
               COUNT(ip.produto_id) as vendas
        FROM produto p
        JOIN item_pedido ip ON p.id = ip.produto_id
        GROUP BY p.id, p.nome, p.preco
        ORDER BY vendas DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findProdutosMaisVendidos();

    // =========================
    // FILTROS
    // =========================

    List<Produto> findByStatus(Tipo tipo);

    Optional<Produto> findById(Long id);
}