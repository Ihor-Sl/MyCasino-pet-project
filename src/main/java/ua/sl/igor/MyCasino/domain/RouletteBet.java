package ua.sl.igor.MyCasino.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.sl.igor.MyCasino.domain.enums.BetColor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity(name = "roulette_bet")
public class RouletteBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    @NotNull(message = "Bid must have an owner")
    private Player owner;

    @Column(name = "volume")
    @Min(value = 0, message = "volume must be greater than 0")
    private long volume;

    @Enumerated(EnumType.STRING)
    private BetColor betColor;

}
