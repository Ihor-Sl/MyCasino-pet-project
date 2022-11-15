package ua.sl.igor.MyCasino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.sl.igor.MyCasino.DTO.ExceptionDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.services.PlayerService;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PlayerService playerService;

    @Autowired
    public AdminController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String admin(@AuthenticationPrincipal Player admin,
                        Model model
    ) {
        model.addAttribute("admin", admin);
        model.addAttribute("playerList", playerService.findAll());
        return "admin";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @GetMapping("/ban/{id}")
    public void ban(@PathVariable long id) {
        playerService.ban(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @GetMapping("/unBan/{id}")
    public void unBan(@PathVariable long id) {
        playerService.unBan(id);
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    @ResponseBody
    public ExceptionDTO handlePlayerNotFoundException() {
        return new ExceptionDTO("Player does not exist", 400);
    }
}
