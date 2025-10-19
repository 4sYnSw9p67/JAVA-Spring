package main.web;

import jakarta.servlet.http.HttpSession;
import main.model.House;
import main.model.Wizard;
import main.service.WizardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/arena")
public class ArenaController {

    private final WizardService wizardService;

    @Autowired
    public ArenaController(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @GetMapping
    public ModelAndView getArenaPage(HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        Wizard currentWizard = wizardService.getById(wizardId);

        // Get wizards for each house, sorted by total power (desc), then username (asc)
        List<Wizard> gryffindorWizards = getSortedWizards(House.GRYFFINDOR);
        List<Wizard> slytherinWizards = getSortedWizards(House.SLYTHERIN);
        List<Wizard> ravenclawWizards = getSortedWizards(House.RAVENCLAW);
        List<Wizard> hufflepuffWizards = getSortedWizards(House.HUFFLEPUFF);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("arena");
        modelAndView.addObject("wizard", currentWizard);
        modelAndView.addObject("gryffindorWizards", gryffindorWizards);
        modelAndView.addObject("slytherinWizards", slytherinWizards);
        modelAndView.addObject("ravenclawWizards", ravenclawWizards);
        modelAndView.addObject("hufflepuffWizards", hufflepuffWizards);

        return modelAndView;
    }

    private List<Wizard> getSortedWizards(House house) {
        List<Wizard> wizards = wizardService.getAllByHouse(house);
        wizards.sort(Comparator
                .comparingInt((Wizard w) -> w.getSpells().stream()
                        .mapToInt(spell -> spell.getPower())
                        .sum())
                .reversed()
                .thenComparing(Wizard::getUsername));
        return wizards;
    }
}
