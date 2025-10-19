package main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import main.model.House;
import main.model.WizardAlignment;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, max = 12, message = "Username must be between 6 and 12 characters.")
    private String username;

    @NotBlank
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters.")
    @Pattern(regexp = "\\d{6}", message = "Password must contain only digits.")
    private String password;

    @NotBlank
    @URL(message = "Avatar URL must be a valid URL.")
    private String avatarUrl;

    @NotNull(message = "House must be selected.")
    private House house;

    @NotNull(message = "Alignment must be selected.")
    private WizardAlignment alignment;
}
