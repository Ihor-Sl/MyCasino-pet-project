package ua.sl.igor.MyCasino.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.sl.igor.MyCasino.DTO.MakeBetDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.enums.BetColor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RouletteServiceTest {

    private final RouletteService rouletteService;
    private final PlayerService playerService;

    @Autowired
    RouletteServiceTest(RouletteService rouletteService, PlayerService playerService) {
        this.rouletteService = rouletteService;
        this.playerService = playerService;
    }

    @Test
    void makeBetAndCalculate() {
        Player player = playerService.findByName("__testUser");
        makeTestBet(player, 100, BetColor.RED, true);
        makeTestBet(player, 200, BetColor.BLACK, true);
        makeTestBet(player, 300, BetColor.GREEN, true);
        makeTestBet(player, 400, BetColor.RED, false);
        makeTestBet(player, 500, BetColor.BLACK, false);
        makeTestBet(player, 600, BetColor.GREEN, false);
    }

    private void makeTestBet(Player player, long amount, BetColor betColor, boolean isWin) {
        player = playerService.findById(player.getId());
        playerService.increasePlayerBalance(player.getId(), amount);

        long oldBalance = player.getBalance();
        int winNumber = 0;
        int winCoef = 14;

        switch (betColor) {
            case RED -> {
                if (isWin) winNumber = 1;
                else winNumber = 8;
                winCoef = 2;
            }
            case BLACK -> {
                if (isWin) winNumber = 8;
                else winNumber = 1;
                winCoef = 2;
            }
            case GREEN -> {
                if (!isWin) winNumber = 1;
            }
        }
        MakeBetDTO makeBetDTO = new MakeBetDTO(amount, betColor);
        rouletteService.makeBet(player, makeBetDTO);

        rouletteService.calculate(winNumber);

        player = playerService.findById(player.getId());
        if (isWin) {
            assertEquals(player.getBalance(), oldBalance + amount * winCoef);
            playerService.decreasePlayerBalance(player.getId(), amount * winCoef);
        } else {
            assertEquals(player.getBalance(), oldBalance);
        }
    }
}