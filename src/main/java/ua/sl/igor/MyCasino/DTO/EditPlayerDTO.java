package ua.sl.igor.MyCasino.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPlayerDTO {
    @NotNull(message = "Name required!")
    @Size(min = 2, max = 100, message = "Length must be greater than 2 and lower than 100!")
    private String name;

    @NotEmpty(message = "Email required!")
    @Email(message = "Email must be valid!")
    private String email;
}
