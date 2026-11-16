
package com.ecommerce.gestao.service;

import com.ecommerce.gestao.entity.Produto;
import com.ecommerce.gestao.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    // Lista todos os produtos
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // Pesquisa produtos pelo nome
    public List<Produto> pesquisar(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    // Busca produto pelo ID
    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado"));
    }

    // Salva ou atualiza produto
    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    // Exclui produto
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    // Indicador: quantidade de produtos cadastrados
    public long contarProdutos() {
        return repository.count();
    }

    // Indicador: soma das unidades em estoque
    public int somarEstoque() {
        return repository.findAll()
                .stream()
                .mapToInt(p -> p.getEstoque() == null
                        ? 0 : p.getEstoque())
                .sum();
    }

    // Indicador: produtos com estoque baixo
    public long contarEstoqueBaixo() {
        return repository.findAll()
                .stream()
                .filter(p -> p.getEstoque() != null
                        && p.getEstoque() <= 5)
                .count();
    }

    // Indicador: valor financeiro total do estoque
    public BigDecimal calcularValorEstoque() {
        return repository.findAll()
                .stream()
                .map(p -> {
                    BigDecimal preco = p.getPreco() == null
                            ? BigDecimal.ZERO : p.getPreco();

                    int estoque = p.getEstoque() == null
                            ? 0 : p.getEstoque();

                    return preco.multiply(
                            BigDecimal.valueOf(estoque));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
