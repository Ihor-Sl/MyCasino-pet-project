package ua.sl.igor.MyCasino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.sl.igor.MyCasino.DTO.BetDTO;
import ua.sl.igor.MyCasino.DTO.ExceptionDTO;
import ua.sl.igor.MyCasino.DTO.MakeBetDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.services.PlayerService;
import ua.sl.igor.MyCasino.services.RouletteService;
import ua.sl.igor.MyCasino.util.exceptions.MakeBetException;
import ua.sl.igor.MyCasino.util.exceptions.NotEnoughMoneyException;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;
import ua.sl.igor.MyCasino.util.exceptions.WaitBeginningOfTheGame;

import javax.validation.Valid;

@Controller
@RequestMapping("/roulette")
public class RouletteController {

    private final RouletteService rouletteService;
    private final PlayerService playerService;

    @Autowired
    public RouletteController(RouletteService rouletteService, PlayerService playerService) {
        this.rouletteService = rouletteService;
        this.playerService = playerService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal Player player,
                        Model model
    ) {
        model.addAttribute("player", playerService.findById(player.getId()).orElseThrow(PlayerNotFoundException::new));
        model.addAttribute("lastWinNumber", rouletteService.getLastWinNumber());
        return "roulette";
    }

    @MessageMapping("/roulette/bets")
    @SendToUser("/topic/console")
    public BetDTO makeBet(@RequestBody @Valid MakeBetDTO makeBetDTO,
                          Authentication authentication
    ) {
        Player player = (Player) authentication.getPrincipal();
        return rouletteService.makeBet(player, makeBetDTO);
    }

    @MessageExceptionHandler(NotEnoughMoneyException.class)
    @SendToUser("/topic/console")
    public ExceptionDTO handleNotEnoughMoneyException() {
        return new ExceptionDTO("Not enough money!", 400);
    }

    @MessageExceptionHandler({MakeBetException.class, MessageConversionException.class})
    @SendToUser("/topic/console")
    public ExceptionDTO handleMakeBidException() {
        return new ExceptionDTO("Something went wrong!", 400);
    }

    @MessageExceptionHandler(WaitBeginningOfTheGame.class)
    @SendToUser("/topic/console")
    public ExceptionDTO handleWaitBeginningOfTheGame() {
        return new ExceptionDTO("Wait beginning of the game!", 400);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/topic/console")
    public ExceptionDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder sB = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String message = error.getDefaultMessage();
            sB.append(message);
            sB.append(" ");
        });
        return new ExceptionDTO(sB.toString(), 400);
    }

}
