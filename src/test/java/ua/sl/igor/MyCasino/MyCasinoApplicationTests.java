package ua.sl.igor.MyCasino;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.sl.igor.MyCasino.DTO.MakeBetDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.enums.BetColor;
import ua.sl.igor.MyCasino.services.PlayerService;
import ua.sl.igor.MyCasino.services.RouletteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class MyCasinoApplicationTests {

    private final RouletteService rouletteService;
    private final PlayerService playerService;

    @Autowired
    MyCasinoApplicationTests(RouletteService rouletteService, PlayerService playerService) {
        this.rouletteService = rouletteService;
        this.playerService = playerService;
    }

    @Test
    public void testMakeBets() {
        Player player = playerService.findByEmail("test@test").get();
        makeTestBet(player, 100, BetColor.RED, true);
        makeTestBet(player, 200, BetColor.BLACK, true);
        makeTestBet(player, 300, BetColor.GREEN, true);
        makeTestBet(player, 400, BetColor.RED, false);
        makeTestBet(player, 500, BetColor.BLACK, false);
        makeTestBet(player, 600, BetColor.GREEN, false);
    }

    @Test
    public void mockitoTest() {
        Player player = Mockito.mock(Player.class);
        when(player.getId()).thenReturn(1L);
        player.getId();
        player.getId();
        // times(), never(), atLeast(), atMost(), only()!
        verify(player, times(2)).getId();
    }

    private void makeTestBet(Player player, long amount, BetColor betColor, boolean isWin) {
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
        playerService.increasePlayerBalance(player.getId(), amount);

        MakeBetDTO makeBetDTO = new MakeBetDTO(amount, betColor);
        rouletteService.makeBet(player, makeBetDTO);

        rouletteService.calculate(winNumber);

        player = playerService.findById(player.getId()).get();
        if (isWin) {
            assertEquals(player.getBalance(), amount * winCoef);
            playerService.decreasePlayerBalance(player.getId(), amount * winCoef);
        } else {
            assertEquals(player.getBalance(), oldBalance);
        }
    }

}
