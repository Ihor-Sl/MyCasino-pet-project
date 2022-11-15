package ua.sl.igor.MyCasino.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.sl.igor.MyCasino.domain.RouletteBet;

@Repository
public interface RouletteBetRepository extends JpaRepository<RouletteBet, Long> {
}
