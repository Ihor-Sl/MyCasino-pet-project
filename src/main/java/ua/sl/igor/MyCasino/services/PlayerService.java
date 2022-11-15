package ua.sl.igor.MyCasino.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.enums.Role;
import ua.sl.igor.MyCasino.repositories.PlayerRepository;
import ua.sl.igor.MyCasino.util.exceptions.NotEnoughMoneyException;
import ua.sl.igor.MyCasino.util.exceptions.PlayerNotFoundException;

import java.util.List;
import java.util.Optional;

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

    public void decreasePlayerBalance(long id, long amount) {
        Player player = findById(id).orElseThrow(PlayerNotFoundException::new);
        if (player.getBalance() < amount) {
            throw new NotEnoughMoneyException();
        }
        player.setBalance(player.getBalance() - amount);
        save(player);
    }

    public void increasePlayerBalance(long id, long amount) {
        Player player = findById(id).orElseThrow(PlayerNotFoundException::new);
        player.setBalance(player.getBalance() + amount);
        save(player);
    }

    public boolean changePassword(Player player, String oldPassword, String newPassword) {
        Player playerDB = findById(player.getId()).orElseThrow(PlayerNotFoundException::new);
        if (passwordEncoder.matches(oldPassword, playerDB.getPassword())) {
            playerDB.setPassword(passwordEncoder.encode(newPassword));
            save(playerDB);
            return true;
        }
        return false;
    }

    public void ban(long id) {
        Player player = findById(id).orElseThrow(PlayerNotFoundException::new);
        jdbcTemplate.update("DELETE FROM spring_session WHERE principal_name=?", player.getEmail());
        player.setAccountNonLocked(false);
        save(player);
    }

    public void unBan(long id) {
        Player player = findById(id).orElseThrow(PlayerNotFoundException::new);
        player.setAccountNonLocked(true);
        save(player);
    }

    public void register(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        player.setRole(Role.ROLE_USER);
        player.setAccountNonLocked(true);
        save(player);
    }

    public String changePlayer(Player player,
                               String newEmail,
                               String newName,
                               BindingResult bindingResult,
                               Model model
    ) {
        player = findById(player.getId()).orElseThrow(PlayerNotFoundException::new);
        model.addAttribute("player", player);
        if (player.getName().equals(newName) && player.getEmail().equals(newEmail)) {
            return "redirect:/profile";
        } else {
            boolean isSomeIsTaken = false;
            String sameName = null;
            String sameEmail = null;
            Optional<Player> sameNamePlayer = findByName(newName);
            Optional<Player> sameEmailPlayer = findByEmail(newEmail);
            if (sameNamePlayer.isPresent()) {
                sameName = sameNamePlayer.get().getName();
            }
            if (sameEmailPlayer.isPresent()) {
                sameEmail = sameEmailPlayer.get().getEmail();
            }
            if (newName.equals(sameName) && !(player.getName().equals(sameName))) {
                model.addAttribute("nameError", "This name is already taken!");
                isSomeIsTaken = true;
            }
            if (newEmail.equals(sameEmail) && !(player.getEmail().equals(sameEmail))) {
                model.addAttribute("emailError", "This email is already taken!");
                isSomeIsTaken = true;
            }
            if (bindingResult.hasErrors()) {
                return "editProfile";
            }
            if (isSomeIsTaken) {
                return "editProfile";
            }
        }

        player.setName(newName);
        player.setEmail(newEmail);
        save(player);

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(player, oldAuth.getCredentials(), oldAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/profile";
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
        if (findByEmail(player.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "This email is already taken!");
        }
        if (findByName(player.getName()).isPresent()) {
            errors.rejectValue("name", "", "This name is already taken!");
        }

    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> findById(long id) {
        return playerRepository.findById(id);
    }

    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    public Optional<Player> findByName(String name) {
        return playerRepository.findByName(name);
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }
}
