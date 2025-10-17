package main.web.resolver;

import main.model.Player;
import main.model.PlayerRole;
import main.model.Quest;
import main.service.QuestService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AdventurerHomeResolver implements HomeResolver {

    private final QuestService questService;

    public AdventurerHomeResolver(QuestService questService) {
        this.questService = questService;
    }

    @Override
    public boolean supports(PlayerRole playerRole) {
        return playerRole == PlayerRole.ADVENTURER;
    }

    @Override
    public String getViewName() {
        return "adventurer-home";
    }

    @Override
    public Map<String, Object> getModelData(Player player) {

        List<Quest> allQuests = questService.getAllByClass(player.getPlayerClass());
        List<Quest> allMyCapturedQuests = allQuests.stream()
                .filter(quest -> quest.getCapturer() != null && quest.getCapturer().getId().equals(player.getId()))
                .toList();

        return Map.of(
                "adventurer", player,
                "quests", allQuests,
                "allMyCapturedQuests", allMyCapturedQuests
        );
    }
}
