package main.service;

import jakarta.validation.Valid;
import main.model.Player;
import main.model.PlayerClass;
import main.model.Quest;
import main.repository.QuestRepository;
import main.web.dto.CreateQuest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final ItemService itemService;
    private final PlayerService playerService;

    @Autowired
    public QuestService(QuestRepository questRepository, ItemService itemService, PlayerService playerService) {
        this.questRepository = questRepository;
        this.itemService = itemService;
        this.playerService = playerService;
    }


    public List<Quest> getAllQuests() {

        return questRepository.findAllByOrderByCreatedOnDescXpDesc();
    }

    public void createNewQuest(CreateQuest createQuest, Player player) {

        Quest quest = Quest.builder()
                .title(createQuest.getTitle())
                .description(createQuest.getDescription())
                .xp(createQuest.getXp())
                .bannerUrl(createQuest.getUrl())
                .eligibleClass(createQuest.getEligibleClass())
                .rewardItem(itemService.getById(createQuest.getRewardItemId()))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .createdBy(player.getNickname())
                .updatedBy(player.getNickname())
                .build();

        questRepository.save(quest);
    }

    public List<Quest> getAllByClass(PlayerClass playerClass) {

        return questRepository.findAllByEligibleClassOrderByCreatedOnDesc(playerClass);
    }

    @Transactional
    public void captureQuest(UUID id, Player player) {

        Quest quest = questRepository.findById(id).orElseThrow(() -> new RuntimeException("Quest not found"));

        if (quest.getCapturer() != null) {
            throw new RuntimeException("The quest is already captured!");
        }

        quest.setCapturer(player);
        //Player earns: quest.xpPoints * rewardItem.xpBonusMultiplier
        double xpFromThisQuest = quest.getXp() * quest.getRewardItem().getXpBonusMultiplier();
        player.setXp(player.getXp() + xpFromThisQuest);

        questRepository.save(quest);
        playerService.update(player);
    }
}
