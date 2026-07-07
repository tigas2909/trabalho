-- ============================================
-- Script de Criação do Banco de Dados
-- Projeto: Loja Virtual (Trabalho Web III)
-- ============================================

DROP DATABASE IF EXISTS loja_virtual;
CREATE DATABASE loja_virtual CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE loja_virtual;

-- ============================================
-- Tabela: user
-- (mapeada pela entidade User)
-- ============================================
CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_user ENUM('CLIENTE','lOJISTA') NOT NULL DEFAULT 'CLIENTE',
    status ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO'
) ENGINE=InnoDB;

-- ============================================
-- Tabela: clientes
-- (OneToOne com user)
-- ============================================
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

-- ============================================
-- Tabela: lojistas
-- (OneToOne com user; sem campos extras além do vínculo)
-- ============================================
CREATE TABLE lojistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

-- ============================================
-- Tabela: categoria
-- ============================================
CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- ============================================
-- Tabela: produto
-- ============================================
CREATE TABLE produto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    estoque INT NOT NULL,
    qtd_minima INT NOT NULL,
    categoria_id BIGINT NOT NULL,
    status ENUM('ATIVO','INATIVO') NOT NULL DEFAULT 'ATIVO',
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
) ENGINE=InnoDB;

-- ============================================
-- Tabela: pedidos
-- ============================================
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    pedido_status ENUM('ABERTO','CONCLUIDO','CANCELADO') NOT NULL DEFAULT 'ABERTO',
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
) ENGINE=InnoDB;

-- ============================================
-- Tabela: item_pedido
-- ============================================
CREATE TABLE item_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    pedido_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
) ENGINE=InnoDB;

-- ============================================
-- Índices adicionais (recomendados)
-- ============================================
CREATE INDEX idx_produto_nome ON produto(nome);
CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(pedido_id);
CREATE INDEX idx_item_pedido_produto ON item_pedido(produto_id);

-- ============================================
-- Observações:
-- - Execute este script no MySQL antes de rodar a aplicação (ou use createDatabaseIfNotExist).
-- - A aplicação usa spring.jpa.hibernate.ddl-auto=update, então pode ajustar colunas extras.
-- - Enums são armazenados como STRING (nomes dos valores dos enums).
-- - Para popular dados iniciais (categorias, produtos, usuários), use inserts adicionais ou a UI.
-- ============================================
