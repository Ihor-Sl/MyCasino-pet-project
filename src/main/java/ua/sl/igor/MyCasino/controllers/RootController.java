package ua.sl.igor.MyCasino.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.sl.igor.MyCasino.domain.Player;

@Controller
public class RootController {
    @GetMapping
    public String index(@AuthenticationPrincipal Player player,
                        Model model
    ) {
        model.addAttribute("player", player);
        return "index";
    }
}
