package ua.sl.igor.MyCasino.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.RouletteBet;
import ua.sl.igor.MyCasino.domain.enums.BetColor;
import ua.sl.igor.MyCasino.repositories.RouletteBetRepository;

import java.util.List;

@Service
public class RouletteBetsService {

    private final RouletteBetRepository rouletteBetRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RouletteBetsService(RouletteBetRepository rouletteBetRepository, JdbcTemplate jdbcTemplate) {
        this.rouletteBetRepository = rouletteBetRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void registerBet(Player player, long amount, BetColor betColor) {
        RouletteBet rouletteBet = new RouletteBet();
        rouletteBet.setOwner(player);
        rouletteBet.setVolume(amount);
        rouletteBet.setBetColor(betColor);
        rouletteBetRepository.save(rouletteBet);
    }

    public List<RouletteBet> findAll() {
        return rouletteBetRepository.findAll();
    }

    public void clearAllBids() {
        jdbcTemplate.update("TRUNCATE roulette_bet; ALTER SEQUENCE roulette_bet_id_seq RESTART WITH 1;");
    }
}
