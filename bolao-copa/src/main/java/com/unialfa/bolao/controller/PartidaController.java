package com.unialfa.bolao.controller;

import com.unialfa.bolao.model.FasePartida;
import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.StatusPartida;
import com.unialfa.bolao.service.PartidaService;
import com.unialfa.bolao.service.SelecaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/partidas")
public class PartidaController {

    private final PartidaService service;
    private final SelecaoService selecaoService;

    public PartidaController(PartidaService service, SelecaoService selecaoService) {
        this.service = service;
        this.selecaoService = selecaoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("partidas", service.listar());
        return "partida/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preencherFormulario(model, new Partida());
        return "partida/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        preencherFormulario(model, service.buscar(id));
        return "partida/form";
    }

    @PostMapping("/salvar")
    public String salvar(Partida partida, RedirectAttributes redirectAttributes) {
        try {
            service.salvar(partida);
            redirectAttributes.addFlashAttribute("sucesso", "Partida salva com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/partidas";
    }

    @GetMapping("/remover/{id}")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.remover(id);
            redirectAttributes.addFlashAttribute("sucesso", "Partida removida com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/partidas";
    }

    private void preencherFormulario(Model model, Partida partida) {
        model.addAttribute("partida", partida);
        model.addAttribute("selecoes", selecaoService.listar());
        model.addAttribute("fases", FasePartida.values());
        model.addAttribute("statusList", StatusPartida.values());
    }
}
