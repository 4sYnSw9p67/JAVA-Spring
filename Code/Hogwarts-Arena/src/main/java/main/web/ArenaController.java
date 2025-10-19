package main.web;

import jakarta.servlet.http.HttpSession;
import main.model.House;
import main.model.Wizard;
import main.service.WizardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getArenaPage(@RequestParam(value = "house", required = false) House house,
            HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        Wizard currentWizard = wizardService.getById(wizardId);

        // If no house is selected, show current wizard's house
        House selectedHouse = house != null ? house : currentWizard.getHouse();
        List<Wizard> wizardsInHouse = wizardService.getAllByHouse(selectedHouse);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("arena");
        modelAndView.addObject("wizard", currentWizard);
        modelAndView.addObject("selectedHouse", selectedHouse);
        modelAndView.addObject("wizardsInHouse", wizardsInHouse);

        return modelAndView;
    }
}
