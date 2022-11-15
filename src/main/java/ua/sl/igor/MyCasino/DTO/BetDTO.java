package ua.sl.igor.MyCasino.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.domain.enums.BetColor;

@Data
public class BetDTO {
    @JsonIgnore
    private Player owner;

    private String ownerName;
    private long amount;
    private BetColor betColor;
    private long currentBalance;

    public BetDTO(Player owner, long amount, BetColor betColor) {
        this.owner = owner;
        this.ownerName = owner.getName();
        this.amount = amount;
        this.betColor = betColor;
        this.currentBalance = owner.getBalance();
    }
}
