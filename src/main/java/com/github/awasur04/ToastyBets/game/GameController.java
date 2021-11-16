package com.github.awasur04.ToastyBets.game;

import com.github.awasur04.ToastyBets.models.Game;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Controller
public class GameController {
    private HashMap<Long, Game> weekSchedule = new HashMap<>();
    private int weekNumber;



    public ArrayList<Game> getWeekSchedule() {
        ArrayList<Game> temp = new ArrayList<Game>();
        for(Game currentGame : this.weekSchedule.values()) {
            temp.add(currentGame);
        }
        Collections.sort(temp);
        return temp;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void addGame(Long matchId, Game game) {
        try {
            if (game != null && matchId != null) {
                this.weekSchedule.put(matchId, game);
            }
        }catch (Exception e) {
            LogManager.error("Failed to add game to weekly schedule", e.getStackTrace().toString());
        }

    }
}
