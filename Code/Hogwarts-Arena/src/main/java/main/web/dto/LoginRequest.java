package main.java.main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 6, max = 12, message = "Username must be between 6 and 12 characters.")
    private String username;

    @NotBlank
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters.")
    @Pattern(regexp = "\\d{6}", message = "Password must contain only digits.")
    private String password;
}
