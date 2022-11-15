package ua.sl.igor.MyCasino.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.sl.igor.MyCasino.domain.enums.BetColor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakeBetDTO {
    @Max(value = 1_000_000_000, message = "Max bet volume 1,000,000,000$")
    @Min(value = 1, message = "Min bet volume 1$")
    private long amount;
    @NotNull(message = "Select bet color!")
    private BetColor betColor;
}
