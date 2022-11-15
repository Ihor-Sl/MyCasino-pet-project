package ua.sl.igor.MyCasino.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionDTO {
    private String error;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalDateTime time = LocalDateTime.now();
    private int statusCode;

    public ExceptionDTO(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }
}
