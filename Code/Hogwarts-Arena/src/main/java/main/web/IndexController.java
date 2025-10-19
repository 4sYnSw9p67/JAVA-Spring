package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.exception.AuthenticationException;
import main.exception.RegistrationException;
import main.model.Wizard;
import main.service.WizardService;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IndexController {

    private final WizardService wizardService;

    @Autowired
    public IndexController(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        try {
            wizardService.register(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return new ModelAndView("redirect:/login");
        } catch (RegistrationException e) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        try {
            Wizard wizard = wizardService.login(loginRequest);
            session.setAttribute("user_id", wizard.getId());
            return new ModelAndView("redirect:/home");
        } catch (AuthenticationException e) {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
