
package com.ecommerce.gestao.controller;

import com.ecommerce.gestao.entity.Produto;
import com.ecommerce.gestao.service.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            Model model) {

        if (nome != null && !nome.isBlank()) {
            model.addAttribute("produtos", service.pesquisar(nome));
        } else {
            model.addAttribute("produtos", service.listarTodos());
        }

        return "produtos/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        return "produtos/formulario";
    }

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
