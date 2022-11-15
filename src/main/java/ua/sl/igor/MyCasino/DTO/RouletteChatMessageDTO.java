package ua.sl.igor.MyCasino.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RouletteChatMessageDTO {
    private String message;
    private String from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalDateTime date = LocalDateTime.now();

    public RouletteChatMessageDTO(String message, String from) {
        this.message = message;
        this.from = from;
    }
}
