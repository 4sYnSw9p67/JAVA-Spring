package main.web;

import jakarta.servlet.http.HttpSession;
import main.exception.SpellLearningException;
import main.model.Spell;
import main.model.Wizard;
import main.property.SpellsProperties;
import main.service.SpellService;
import main.service.WizardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final WizardService wizardService;
    private final SpellService spellService;
    private final SpellsProperties spellsProperties;

    @Autowired
    public HomeController(WizardService wizardService, SpellService spellService, SpellsProperties spellsProperties) {
        this.wizardService = wizardService;
        this.spellService = spellService;
        this.spellsProperties = spellsProperties;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        Wizard wizard = wizardService.getById(wizardId);

        // Get learned spells sorted by power descending
        List<Spell> learnedSpells = wizard.getSpells().stream()
                .sorted(Comparator.comparingInt(Spell::getPower).reversed())
                .collect(Collectors.toList());

        // Get available spells (not yet learned)
        List<String> learnedSpellCodes = learnedSpells.stream()
                .map(Spell::getCode)
                .collect(Collectors.toList());

        List<SpellsProperties.SpellDefinition> availableSpells = spellsProperties.getSpells().stream()
                .filter(s -> !learnedSpellCodes.contains(s.getCode()))
                .sorted(Comparator.comparingInt(SpellsProperties.SpellDefinition::getMinLearned))
                .collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("wizard", wizard);
        modelAndView.addObject("learnedSpells", learnedSpells);
        modelAndView.addObject("availableSpells", availableSpells);

        return modelAndView;
    }

    @PostMapping("/home/learn-spell")
    public ModelAndView learnSpell(@RequestParam("spellCode") String spellCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UUID wizardId = (UUID) session.getAttribute("user_id");

        try {
            spellService.learnSpell(wizardId, spellCode);
            redirectAttributes.addFlashAttribute("successMessage", "Spell learned successfully!");
        } catch (SpellLearningException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return new ModelAndView("redirect:/home");
    }
}
