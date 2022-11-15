package ua.sl.igor.MyCasino.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickDTO {
    private int timeLeft;
    private Integer winNumber;
}
