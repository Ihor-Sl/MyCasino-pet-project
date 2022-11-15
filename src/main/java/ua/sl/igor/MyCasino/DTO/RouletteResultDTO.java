package ua.sl.igor.MyCasino.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouletteResultDTO {
    private boolean isWin;
    private long currentBalance;
}
