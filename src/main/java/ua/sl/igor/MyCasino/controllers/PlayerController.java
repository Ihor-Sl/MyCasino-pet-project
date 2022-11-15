package ua.sl.igor.MyCasino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.sl.igor.MyCasino.DTO.EditPlayerDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.services.PlayerService;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;

import javax.validation.Valid;

@Controller
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal Player player, Model model) {
        model.addAttribute("player", playerService.findById(player.getId()).orElseThrow(PlayerNotFoundException::new));
        return "profile";
    }

    @PostMapping("/add100")
    public String add100(@AuthenticationPrincipal Player player) {
        playerService.increasePlayerBalance(player.getId(), 100);
        return "redirect:/profile";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("player") Player player) {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerPlayer(@ModelAttribute("player") @Valid Player player, BindingResult bindingResult) {
        playerService.validate(player, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        playerService.register(player);
        return "redirect:/login";
    }

    @GetMapping("/editProfile")
    private String editProfile(@AuthenticationPrincipal Player player, @ModelAttribute("editPlayer") EditPlayerDTO editPlayerDTO, Model model) {
        model.addAttribute("player", playerService.findById(player.getId()).orElseThrow(PlayerNotFoundException::new));
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfileConfirm(@AuthenticationPrincipal Player player,
                                     @ModelAttribute("editPlayer") @Valid EditPlayerDTO editPlayerDTO,
                                     BindingResult bindingResult,
                                     Model model
    ) {
        return playerService.changePlayer(player, editPlayerDTO.getEmail(), editPlayerDTO.getName(), bindingResult, model);
    }

    @PostMapping("/changePassword")
    public String editPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, @AuthenticationPrincipal Player player, Model model) {
        model.addAttribute("player", player);
        if (playerService.changePassword(player, oldPassword, newPassword)) {
            return "redirect:/profile";
        }
        model.addAttribute("passwordError", "Passwords do not match!");
        EditPlayerDTO editPlayerDTO = new EditPlayerDTO(player.getName(), player.getEmail());
        model.addAttribute("editPlayer", editPlayerDTO);
        return "editProfile";
    }
}
