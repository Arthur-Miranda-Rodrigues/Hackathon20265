package com.unialfa.bolao.controller;

import com.unialfa.bolao.model.PerfilUsuario;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuario/list";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", service.buscar(id));
        model.addAttribute("perfis", PerfilUsuario.values());
        return "usuario/form";
    }

    @PostMapping("/salvar")
    public String salvar(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            service.salvarAdmin(usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário salvo com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/bloquear/{id}")
    public String bloquear(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.bloquear(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário bloqueado.");
        return "redirect:/usuarios";
    }

    @PostMapping("/desbloquear/{id}")
    public String desbloquear(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.desbloquear(id);
        redirectAttributes.addFlashAttribute("sucesso", "Usuário desbloqueado.");
        return "redirect:/usuarios";
    }
}
