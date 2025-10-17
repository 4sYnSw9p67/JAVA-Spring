package main.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import main.model.Player;
import main.service.ItemService;
import main.service.PlayerService;
import main.web.dto.ItemCreateRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final PlayerService playerService;

    public ItemController(ItemService itemService, PlayerService playerService) {
        this.itemService = itemService;
        this.playerService = playerService;
    }

    @GetMapping()
    public ModelAndView getItemsPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("itemCreateRequest", new ItemCreateRequest());
        modelAndView.setViewName("items");
        modelAndView.addObject("allItems", itemService.getAllItems());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView createNewItem(@Valid ItemCreateRequest itemCreateRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("items");
            modelAndView.addObject("allItems", itemService.getAllItems());
            return modelAndView;
        }

        UUID playerId = (UUID) session.getAttribute("user_id");
        Player player = playerService.getById(playerId);
        itemService.create(itemCreateRequest, player);

        return new ModelAndView("redirect:/items");
    }
}
