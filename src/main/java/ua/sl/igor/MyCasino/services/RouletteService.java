package ua.sl.igor.MyCasino.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.sl.igor.MyCasino.DTO.BetDTO;
import ua.sl.igor.MyCasino.DTO.MakeBetDTO;
import ua.sl.igor.MyCasino.DTO.RouletteResultDTO;
import ua.sl.igor.MyCasino.DTO.TickDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.RouletteBet;
import ua.sl.igor.MyCasino.domain.enums.BetColor;
import ua.sl.igor.MyCasino.util.exceptions.MakeBetException;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;
import ua.sl.igor.MyCasino.util.exceptions.WaitBeginningOfTheGame;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RouletteService implements Runnable {

    private final PlayerService playerService;
    private final RouletteBetsService rouletteBetsService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private Thread thread;
    private int lastWinNumber;

    public int getLastWinNumber() {
        return lastWinNumber;
    }

    @Autowired
    public RouletteService(PlayerService playerService, RouletteBetsService rouletteBetsService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerService = playerService;
        this.rouletteBetsService = rouletteBetsService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public BetDTO makeBet(Player player, MakeBetDTO makeBetDTO) {
        if (thread != null) {
            if (makeBetDTO.getAmount() < 1) {
                throw new MakeBetException();
            }
            player = playerService.findById(player.getId()).orElseThrow(PlayerNotFoundException::new);
            playerService.decreasePlayerBalance(player.getId(), makeBetDTO.getAmount());
            rouletteBetsService.registerBet(player, makeBetDTO.getAmount(), makeBetDTO.getBetColor());
            return new BetDTO(player, makeBetDTO.getAmount(), makeBetDTO.getBetColor());
        } else {
            throw new WaitBeginningOfTheGame();
        }
    }

    @Transactional
    public void calculate(int winNumber) {
        BetColor winColor = getColorFromNumber(winNumber);
        int winCoefficient = 2;
        if (winNumber == 0) {
            winCoefficient = 14;
        }
        List<RouletteBet> allBets = rouletteBetsService.findAll();
        for (RouletteBet bet : allBets) {
            Player betOwner = bet.getOwner();
            if (bet.getBetColor().equals(winColor)) {
                playerService.increasePlayerBalance(betOwner.getId(), bet.getVolume() * winCoefficient);
                simpMessagingTemplate.convertAndSendToUser(
                        betOwner.getEmail(),
                        "/topic/console",
                        new RouletteResultDTO(true, playerService.findById(betOwner.getId()).get().getBalance()));
            } else {
                simpMessagingTemplate.convertAndSendToUser(
                        betOwner.getEmail(),
                        "/topic/console",
                        "{\"win\": false}");
            }
        }
        rouletteBetsService.clearAllBids();
    }

    private BetColor getColorFromNumber(int number) {
        if (number == 0) {
            return BetColor.GREEN;
        }
        if (number > 7) {
            return BetColor.BLACK;
        }
        if (number <= 7) {
            return BetColor.RED;
        }
        return null;
    }

    private int getRandomNumber() {
        return (int) Math.floor(Math.random() * 15);
    }

    @PostConstruct
    private void spin() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        int result = getRandomNumber();
        for (int i = 20; i > 0; i--) {
            simpMessagingTemplate.convertAndSend("/topic/roulette", new TickDTO(i, null));
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        thread = null;
        lastWinNumber = result;
        simpMessagingTemplate.convertAndSend("/topic/roulette", new TickDTO(0, result));
        calculate(result);
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        spin();
    }
}
