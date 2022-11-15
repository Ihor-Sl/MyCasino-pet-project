package ua.sl.igor.MyCasino.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.sl.igor.MyCasino.domain.Player;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(long id);

    Optional<Player> findByEmail(String email);

    Optional<Player> findByName(String name);
}
