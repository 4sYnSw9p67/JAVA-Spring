package main.web.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import main.model.PlayerClass;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuest {

    @NotBlank
    @Size(min = 6, max = 26)
    private String title;

    @NotNull
    private UUID rewardItemId;

    @NotNull
    private PlayerClass eligibleClass;

    @NotBlank
    @Size(min = 110, max = 128)
    private String description;

    @NotBlank
    @URL
    private String url;

    @Min(1)
    @Max(13)
    private double xp;
}
