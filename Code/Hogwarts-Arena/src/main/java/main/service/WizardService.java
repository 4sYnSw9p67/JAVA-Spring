package main.service;

import main.exception.AuthenticationException;
import main.exception.RegistrationException;
import main.model.*;
import main.property.SpellsProperties;
import main.repository.SpellRepository;
import main.repository.WizardRepository;
import main.web.dto.EditProfileRequest;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WizardService {

    private final WizardRepository wizardRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpellRepository spellRepository;
    private final SpellsProperties spellsProperties;
    private final Random random = new Random();

    @Autowired
    public WizardService(WizardRepository wizardRepository, PasswordEncoder passwordEncoder,
            SpellRepository spellRepository, SpellsProperties spellsProperties) {
        this.wizardRepository = wizardRepository;
        this.passwordEncoder = passwordEncoder;
        this.spellRepository = spellRepository;
        this.spellsProperties = spellsProperties;
    }

    @Transactional
    public Wizard register(RegisterRequest registerRequest) {
        if (wizardRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RegistrationException("Username is already taken.");
        }

        Wizard wizard = Wizard.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .house(registerRequest.getHouse())
                .avatarUrl(registerRequest.getAvatarUrl())
                .alignment(registerRequest.getAlignment())
                .spells(new ArrayList<>())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        wizard = wizardRepository.save(wizard);

        // Auto-learn 1 random spell with minLearned = 0
        List<SpellsProperties.SpellDefinition> starterSpells = spellsProperties.getSpells().stream()
                .filter(s -> s.getMinLearned() == 0)
                .collect(Collectors.toList());

        if (!starterSpells.isEmpty()) {
            SpellsProperties.SpellDefinition randomSpell = starterSpells.get(random.nextInt(starterSpells.size()));

            Spell spell = Spell.builder()
                    .code(randomSpell.getCode())
                    .name(randomSpell.getName())
                    .description(randomSpell.getDescription())
                    .wizard(wizard)
                    .category(SpellCategory.valueOf(randomSpell.getCategory().toUpperCase()))
                    .alignment(SpellAlignment.valueOf(randomSpell.getAlignment().toUpperCase()))
                    .image(randomSpell.getImage())
                    .power(randomSpell.getPower())
                    .createdOn(LocalDateTime.now())
                    .build();

            wizard.getSpells().add(spell);
            spellRepository.save(spell);
            wizard = wizardRepository.save(wizard);
        }

        return wizard;
    }

    public Wizard login(LoginRequest loginRequest) {
        Wizard wizard = wizardRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Incorrect username or password."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), wizard.getPassword())) {
            throw new AuthenticationException("Incorrect username or password.");
        }

        return wizard;
    }

    public Wizard getById(UUID id) {
        return wizardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wizard not found."));
    }

    public Wizard getByUsername(String username) {
        return wizardRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Wizard not found."));
    }

    @Transactional
    public void updateProfile(UUID wizardId, EditProfileRequest editProfileRequest) {
        Wizard wizard = getById(wizardId);

        // Check if new username is already taken by another wizard
        if (!wizard.getUsername().equals(editProfileRequest.getUsername())) {
            if (wizardRepository.findByUsername(editProfileRequest.getUsername()).isPresent()) {
                throw new RegistrationException("Username is already taken.");
            }
        }

        wizard.setUsername(editProfileRequest.getUsername());
        wizard.setAvatarUrl(editProfileRequest.getAvatarUrl());
        wizard.setUpdatedOn(LocalDateTime.now());
        wizardRepository.save(wizard);
    }

    @Transactional
    public void changeAlignment(UUID wizardId, WizardAlignment newAlignment) {
        Wizard wizard = getById(wizardId);
        wizard.setAlignment(newAlignment);
        wizard.setUpdatedOn(LocalDateTime.now());
        wizardRepository.save(wizard);
    }

    public List<Wizard> getAllByHouse(House house) {
        return wizardRepository.findAllByHouseOrderByUsernameAsc(house);
    }
}
