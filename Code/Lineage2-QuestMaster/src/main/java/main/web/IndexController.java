package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.model.Player;
import main.service.PlayerService;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import main.web.resolver.HomeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    private final PlayerService playerService;
    private final List<HomeResolver> homeResolvers;

    @Autowired
    public IndexController(PlayerService playerService, List<HomeResolver> homeResolvers) {
        this.playerService = playerService;
        this.homeResolvers = homeResolvers;
    }

    @GetMapping
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
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        playerService.createNewPlayer(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(LoginRequest loginRequest, HttpSession session) {

        // ВАЖНО:
        // 1. При логин всеки път АКТИВИРАМЕ сесията!!!!!!!!!
        // 2. При логин всеки път добавям нов атрибут към сесията с име "user_id" -> id-то на потребителя

        Player player = playerService.login(loginRequest);
        session.setAttribute("user_id", player.getId());

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {

        UUID playerId = (UUID) session.getAttribute("user_id");
        Player player = playerService.getById(playerId);

        HomeResolver homeResolver = homeResolvers.stream()
                .filter(resolver -> resolver.supports(player.getRole()))
                .findFirst()
                .get();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(homeResolver.getViewName());
        modelAndView.addAllObjects(homeResolver.getModelData(player));

        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }
}
