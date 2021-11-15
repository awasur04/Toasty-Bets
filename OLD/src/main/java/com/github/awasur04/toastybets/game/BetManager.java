package com.github.awasur04.toastybets.game;

import com.github.awasur04.toastybets.models.Team;
import com.github.awasur04.toastybets.services.UpdateOdds;
import com.github.awasur04.toastybets.utilities.LogManager;

import java.util.HashMap;

public class BetManager {
    private GameManager gameManager;
    private UpdateOdds updater;


    public BetManager(GameManager manager) {
        this.gameManager = manager;
        this.updater = new UpdateOdds(this);
        updater.updateOdds();
    }


    public void updateTeamOdds(String teamName, String newOdds) {
        try {
            HashMap<Integer, Team> teamList = gameManager.getTeamList();
            float odds = Float.valueOf(newOdds);
            if (odds >= 0.0) {
                for (Team currentTeam : teamList.values()) {
                    if (currentTeam.getName().equalsIgnoreCase(teamName)) {
                        currentTeam.setOdds(odds);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void resetOdds() {
        for (Team team : gameManager.getTeamList().values()) {
            team.setOdds(0);
        }
    }

    public void createBet(Long userId, String teamAbbreviation, int betAmount) {

    }

}
