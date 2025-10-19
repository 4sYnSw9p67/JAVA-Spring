package main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Wizard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String avatarUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WizardAlignment alignment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private House house;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "wizard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spell> spells = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

}
