package com.unialfa.bolao.controller;

import com.unialfa.bolao.model.Selecao;
import com.unialfa.bolao.service.FileStorageService;
import com.unialfa.bolao.service.SelecaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/selecoes")
public class SelecaoController {

    private final SelecaoService service;
    private final FileStorageService fileStorageService;

    public SelecaoController(SelecaoService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("selecoes", service.listar());
        return "selecao/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("selecao", new Selecao());
        return "selecao/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("selecao", service.buscar(id));
        return "selecao/form";
    }

    @PostMapping("/salvar")
    public String salvar(Selecao selecao,
                         @RequestParam(value = "bandeiraFile", required = false) MultipartFile bandeiraFile,
                         RedirectAttributes redirectAttributes) {
        try {
            if (bandeiraFile != null && !bandeiraFile.isEmpty()) {
                selecao.setBandeiraUrl(fileStorageService.salvarImagem(bandeiraFile));
            }
            service.salvar(selecao);
            redirectAttributes.addFlashAttribute("sucesso", "Seleção salva com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/selecoes";
    }

    @GetMapping("/remover/{id}")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.remover(id);
            redirectAttributes.addFlashAttribute("sucesso", "Seleção removida com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/selecoes";
    }
}
