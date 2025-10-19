package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.model.Wizard;
import main.model.WizardAlignment;
import main.service.WizardService;
import main.web.dto.EditProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final WizardService wizardService;

    @Autowired
    public ProfileController(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @GetMapping
    public ModelAndView getProfilePage(HttpSession session) {
        UUID wizardId = (UUID) session.getAttribute("user_id");
        Wizard wizard = wizardService.getById(wizardId);

        EditProfileRequest editProfileRequest = EditProfileRequest.builder()
                .username(wizard.getUsername())
                .avatarUrl(wizard.getAvatarUrl())
                .build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("wizard", wizard);
        modelAndView.addObject("editProfileRequest", editProfileRequest);

        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView editProfile(@Valid EditProfileRequest editProfileRequest,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        UUID wizardId = (UUID) session.getAttribute("user_id");

        if (bindingResult.hasErrors()) {
            Wizard wizard = wizardService.getById(wizardId);
            ModelAndView modelAndView = new ModelAndView("profile");
            modelAndView.addObject("wizard", wizard);
            return modelAndView;
        }

        try {
            wizardService.updateProfile(wizardId, editProfileRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return new ModelAndView("redirect:/profile");
    }

    @PostMapping("/change-alignment")
    public ModelAndView changeAlignment(HttpSession session,
            RedirectAttributes redirectAttributes) {
        UUID wizardId = (UUID) session.getAttribute("user_id");

        // Only allow changing from LIGHT to DARK
        Wizard wizard = wizardService.getById(wizardId);
        if (wizard.getAlignment() == WizardAlignment.LIGHT) {
            wizardService.changeAlignment(wizardId, WizardAlignment.DARK);
            redirectAttributes.addFlashAttribute("successMessage", "Alignment changed to Dark!");
        }

        return new ModelAndView("redirect:/profile");
    }
}
