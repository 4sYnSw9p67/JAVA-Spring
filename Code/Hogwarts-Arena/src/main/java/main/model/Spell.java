package main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "wizard_id", nullable = false)
    private Wizard wizard;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpellCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpellAlignment alignment;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private int power;

    @Column(nullable = false)
    private LocalDateTime createdOn;

}
