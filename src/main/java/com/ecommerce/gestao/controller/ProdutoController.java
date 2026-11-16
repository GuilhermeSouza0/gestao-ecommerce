
package com.ecommerce.gestao.controller;

import com.ecommerce.gestao.entity.Produto;
import com.ecommerce.gestao.service.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // Dashboard e listagem de produtos
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            Model model) {

        // Produtos utilizados no gráfico
        List<Produto> todosProdutos = service.listarTodos();

        // Lista filtrada para a tabela
        if (nome != null && !nome.isBlank()) {
            model.addAttribute(
                    "produtos", service.pesquisar(nome));
        } else {
            model.addAttribute("produtos", todosProdutos);
        }

        // Indicadores do dashboard
        model.addAttribute(
                "totalProdutos", service.contarProdutos());

        model.addAttribute(
                "totalUnidades", service.somarEstoque());

        model.addAttribute(
                "estoqueBaixo", service.contarEstoqueBaixo());

        model.addAttribute(
                "valorTotalEstoque",
                service.calcularValorEstoque());

        // Dados para o gráfico
        model.addAttribute("dadosGrafico", todosProdutos);

        // Maior estoque para dimensionar as barras
        int maiorEstoque = todosProdutos.stream()
                .mapToInt(p -> p.getEstoque() == null
                        ? 0 : p.getEstoque())
                .max()
                .orElse(1);

        model.addAttribute(
                "maiorEstoque", Math.max(1, maiorEstoque));

        return "produtos/listar";
    }

    // Abrir formulário de cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/formulario";
    }

    // Abrir formulário de edição
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "produto", service.buscarPorId(id));

        return "produtos/formulario";
    }

    // Salvar ou atualizar produto
    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("produto") Produto produto,
            BindingResult resultado,
            RedirectAttributes redirect) {

        if (resultado.hasErrors()) {
            return "produtos/formulario";
        }

        service.salvar(produto);

        redirect.addFlashAttribute(
                "mensagem", "Produto salvo com sucesso!");

        return "redirect:/produtos";
    }

    // Excluir produto
    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        service.excluir(id);

        redirect.addFlashAttribute(
                "mensagem", "Produto excluído com sucesso!");

        return "redirect:/produtos";
    }
}
