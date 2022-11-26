package ua.sl.igor.MyCasino.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.sl.igor.MyCasino.domain.enums.BetColor;
import ua.sl.igor.MyCasino.repositories.RouletteBetRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RouletteBetsServiceTest {

    private final RouletteBetsService rouletteBetsService;
    private final RouletteBetRepository rouletteBetRepository;
    private final PlayerService playerService;

    @Autowired
    RouletteBetsServiceTest(RouletteBetsService rouletteBetsService, RouletteBetRepository rouletteBetRepository, PlayerService playerService) {
        this.rouletteBetsService = rouletteBetsService;
        this.rouletteBetRepository = rouletteBetRepository;
        this.playerService = playerService;
    }

    @Test
    void registerBet() {
        rouletteBetsService.registerBet(playerService.findByName("__testUser"), 100, BetColor.GREEN);
        assertNotNull(rouletteBetRepository.findById(1L).get());
    }

    @Test
    void clearAllBids() {
        rouletteBetsService.clearAllBids();
        assertEquals(rouletteBetsService.findAll(), Collections.emptyList());
    }
}