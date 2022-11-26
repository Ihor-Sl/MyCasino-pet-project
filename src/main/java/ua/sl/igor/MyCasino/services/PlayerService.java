package ua.sl.igor.MyCasino.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.enums.Role;
import ua.sl.igor.MyCasino.repositories.PlayerRepository;
import ua.sl.igor.MyCasino.util.exceptions.NotEnoughMoneyException;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;

import java.util.List;

@Service
public class PlayerService implements UserDetailsService, Validator {

    private final PlayerRepository playerRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, JdbcTemplate jdbcTemplate, @Lazy PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void increasePlayerBalance(long id, long amount) {
        Player player = findById(id);
        player.setBalance(player.getBalance() + amount);
        save(player);
    }

    public void decreasePlayerBalance(long id, long amount) {
        Player player = findById(id);
        if (player.getBalance() < amount) {
            throw new NotEnoughMoneyException();
        }
        player.setBalance(player.getBalance() - amount);
        save(player);
    }

    public boolean changePassword(Player player, String oldPassword, String newPassword) {
        Player playerDB = findById(player.getId());
        if (passwordEncoder.matches(oldPassword, playerDB.getPassword())) {
            playerDB.setPassword(passwordEncoder.encode(newPassword));
            save(playerDB);
            return true;
        }
        return false;
    }

    public void register(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        player.setRole(Role.ROLE_USER);
        player.setAccountNonLocked(true);
        save(player);
    }

    public void ban(long id) {
        Player player = findById(id);
        jdbcTemplate.update("DELETE FROM spring_session WHERE principal_name=?", player.getEmail());
        player.setAccountNonLocked(false);
        save(player);
    }

    public void unBan(long id) {
        Player player = findById(id);
        player.setAccountNonLocked(true);
        save(player);
    }

    public boolean canPlayerSetThisName(Player player, String name) {
        if (player.getName().equals(name)) return true;
        return !playerRepository.existsByName(name);
    }

    public boolean canPlayerSetThisEmail(Player player, String email) {
        if (player.getEmail().equals(email)) return true;
        return !playerRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return playerRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Player not found!"));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        if (playerRepository.existsByEmail(player.getEmail())) {
            errors.rejectValue("email", "", "This email is already taken!");
        }
        if (playerRepository.existsByName(player.getName())) {
            errors.rejectValue("name", "", "This name is already taken!");
        }

    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Player findById(long id) {
        return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
    }

    public Player findByEmail(String email) {
        return playerRepository.findByEmail(email).orElseThrow(PlayerNotFoundException::new);
    }

    public Player findByName(String name) {
        return playerRepository.findByName(name).orElseThrow(PlayerNotFoundException::new);
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }
}
