package ua.sl.igor.MyCasino.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.Errors;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.repositories.PlayerRepository;
import ua.sl.igor.MyCasino.util.exceptions.NotEnoughMoneyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class PlayerServiceTest {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    Player player;

    @Autowired
    PlayerServiceTest(PlayerService playerService, PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    @BeforeEach
    void setUp() {
        player = playerService.findByName("__testUser");
    }

    @Test
    void increasePlayerBalance() {
        long sum = 100;
        long oldBalance = player.getBalance();
        playerService.increasePlayerBalance(player.getId(), sum);
        long newBalance = playerService.findById(player.getId()).getBalance();
        assertEquals(oldBalance + sum, newBalance);
    }

    @Test
    void decreasePlayerBalance() {
        assertThrows(NotEnoughMoneyException.class, () -> {
            player.setBalance(0);
            playerRepository.save(player);
            playerService.decreasePlayerBalance(player.getId(), 200);
        });

        long sum = 100;
        long oldBalance = player.getBalance();
        playerService.increasePlayerBalance(player.getId(), sum);
        playerService.decreasePlayerBalance(player.getId(), sum);
        long newBalance = playerService.findById(player.getId()).getBalance();
        assertEquals(oldBalance, newBalance);
    }

    @Test
    void changePassword() {
        assertTrue(playerService.changePassword(player, "12345678", "12345678"));
        assertFalse(playerService.changePassword(player, "qwerty", "qwerty"));
    }

    @Test
    void ban() {
        playerService.ban(player.getId());
        assertFalse(playerService.findById(player.getId()).isAccountNonLocked());
    }

    @Test
    void unBan() {
        playerService.unBan(player.getId());
        assertTrue(playerService.findById(player.getId()).isAccountNonLocked());
    }

    @Test
    void canPlayerSetThisName() {
        assertTrue(playerService.canPlayerSetThisName(player, "__testUser"));
        assertFalse(playerService.canPlayerSetThisName(player, "admin"));
    }

    @Test
    void canPlayerSetThisEmail() {
        assertTrue(playerService.canPlayerSetThisEmail(player, "test@test"));
        assertFalse(playerService.canPlayerSetThisEmail(player, "admin@admin"));
    }

    @Test
    void validate() {
        Errors errors = mock(Errors.class);
        playerService.validate(player, errors);
        verify(errors, times(2)).rejectValue(anyString(), anyString(), anyString());
    }
}
