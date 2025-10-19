package main.java.main.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    @NotBlank
    @Size(min = 6, max = 12, message = "Username must be between 6 and 12 characters.")
    private String username;

    @NotBlank
    @URL(message = "Avatar URL must be a valid URL.")
    private String avatarUrl;
}
