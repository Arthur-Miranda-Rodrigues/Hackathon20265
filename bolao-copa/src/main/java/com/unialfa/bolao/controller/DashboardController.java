package com.unialfa.bolao.controller;

import com.unialfa.bolao.service.PalpiteService;
import com.unialfa.bolao.service.PartidaService;
import com.unialfa.bolao.service.RankingService;
import com.unialfa.bolao.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final PalpiteService palpiteService;
    private final PartidaService partidaService;
    private final RankingService rankingService;

    public DashboardController(UsuarioService usuarioService, PalpiteService palpiteService, PartidaService partidaService, RankingService rankingService) {
        this.usuarioService = usuarioService;
        this.palpiteService = palpiteService;
        this.partidaService = partidaService;
        this.rankingService = rankingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", usuarioService.totalUsuarios());
        model.addAttribute("totalPalpites", palpiteService.totalPalpites());
        model.addAttribute("partidasPendentes", partidaService.totalPendentesResultado());
        model.addAttribute("usuariosAtivos24h", usuarioService.usuariosAtivosUltimas24h());
        model.addAttribute("ranking", rankingService.ranking(0, 50).getContent());
        return "dashboard";
    }
}
