package com.unialfa.bolao.controller;

import com.unialfa.bolao.service.PartidaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resultados")
public class ResultadoController {

    private final PartidaService partidaService;

    public ResultadoController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @GetMapping("/{partidaId}")
    public String formulario(@PathVariable Long partidaId, Model model) {
        model.addAttribute("partida", partidaService.buscar(partidaId));
        return "resultado/form";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Long partidaId,
                         @RequestParam Integer golsA,
                         @RequestParam Integer golsB,
                         RedirectAttributes redirectAttributes) {
        try {
            partidaService.salvarResultado(partidaId, golsA, golsB);
            redirectAttributes.addFlashAttribute("sucesso", "Resultado salvo e pontuação recalculada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/partidas";
    }
}
