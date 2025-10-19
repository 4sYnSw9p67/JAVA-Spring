package main.service;

import main.exception.SpellLearningException;
import main.model.Spell;
import main.model.SpellAlignment;
import main.model.SpellCategory;
import main.model.Wizard;
import main.property.SpellsProperties;
import main.repository.SpellRepository;
import main.repository.WizardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SpellService {

    private final SpellRepository spellRepository;
    private final WizardRepository wizardRepository;
    private final SpellsProperties spellsProperties;

    @Autowired
    public SpellService(SpellRepository spellRepository, WizardRepository wizardRepository,
            SpellsProperties spellsProperties) {
        this.spellRepository = spellRepository;
        this.wizardRepository = wizardRepository;
        this.spellsProperties = spellsProperties;
    }

    @Transactional
    public Spell learnSpell(UUID wizardId, String spellCode) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new RuntimeException("Wizard not found."));

        // Find spell definition from properties
        SpellsProperties.SpellDefinition spellDef = spellsProperties.getSpells().stream()
                .filter(s -> s.getCode().equals(spellCode))
                .findFirst()
                .orElseThrow(() -> new SpellLearningException("Spell not found."));

        // Check if wizard already knows this spell
        boolean alreadyKnows = wizard.getSpells().stream()
                .anyMatch(s -> s.getCode().equals(spellCode));
        if (alreadyKnows) {
            throw new SpellLearningException("You already know this spell.");
        }

        // Check if wizard has learned enough spells
        int spellsLearned = wizard.getSpells().size();
        if (spellsLearned < spellDef.getMinLearned()) {
            throw new SpellLearningException(
                    String.format("You must learn %d spells before learning this spell. Current: %d",
                            spellDef.getMinLearned(), spellsLearned));
        }

        // Create and save the spell
        SpellAlignment spellAlignment = SpellAlignment.valueOf(spellDef.getAlignment().toUpperCase());
        Spell spell = Spell.builder()
                .code(spellDef.getCode())
                .name(spellDef.getName())
                .description(spellDef.getDescription())
                .wizard(wizard)
                .category(SpellCategory.valueOf(spellDef.getCategory().toUpperCase()))
                .alignment(spellAlignment)
                .image(spellDef.getImage())
                .power(spellDef.getPower())
                .createdOn(LocalDateTime.now())
                .build();

        wizard.getSpells().add(spell);
        wizard.setUpdatedOn(LocalDateTime.now());

        // If a Light wizard learns a Dark spell, change alignment to Dark
        if (wizard.getAlignment() == main.model.WizardAlignment.LIGHT && spellAlignment == SpellAlignment.DARK) {
            wizard.setAlignment(main.model.WizardAlignment.DARK);
        }

        spellRepository.save(spell);
        wizardRepository.save(wizard);

        return spell;
    }

    public SpellsProperties.SpellDefinition getAvailableSpellByCode(String spellCode) {
        return spellsProperties.getSpells().stream()
                .filter(s -> s.getCode().equals(spellCode))
                .findFirst()
                .orElse(null);
    }
}
