package com.github.awasur04.toastybets.managers;

import com.github.awasur04.toastybets.models.Game;
import com.github.awasur04.toastybets.models.Team;
import com.github.awasur04.toastybets.services.ScheduleService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private Map<Integer, Team> teamList;
    private ArrayList<Game> currentWeekGames;
    private ScheduleService scheduleService;


    public GameManager() {
        this.teamList = new HashMap<>() {{
            put(1, new Team(1,"Atlanta Falcons"));
            put(2, new Team(2, "Buffalo Bills"));
            put(3, new Team(3, "Chicago Bears"));
            put(4, new Team(4, "Cincinnati Bengals"));
            put(5, new Team(5, "Cleveland Browns"));
            put(6, new Team(6, "Dallas Cowboys"));
            put(7, new Team(7, "Denver Broncos"));
            put(8, new Team(8, "Detroit Lions"));
            put(9, new Team(9, "Green Bay Packers"));
            put(10, new Team(10, "Tennessee Titans"));
            put(11, new Team(11, "Indianapolis Colts"));
            put(12, new Team(12, "Kansas City Chiefs"));
            put(13, new Team(13, "Las Vegas Raiders"));
            put(14, new Team(14, "Las Angeles Rams"));
            put(15, new Team(15, "Miami Dolphins"));
            put(16, new Team(16, "Minnesota Vikings"));
            put(17, new Team(17, "New England Patriots"));
            put(18, new Team(18, "New Orleans Saints"));
            put(19, new Team(19, "New York Giants"));
            put(20, new Team(20, "New York Jets"));
            put(21, new Team(21, "Philadelphia Eagles"));
            put(22, new Team(22, "Arizona Cardinals"));
            put(23, new Team(23, "Pittsburgh Steelers"));
            put(24, new Team(24, "Los Angeles Chargers"));
            put(25, new Team(25, "San Francisco 49ers"));
            put(26, new Team(26, "Seattle Seahawks"));
            put(27, new Team(27, "Tampa Bay Buccaneers"));
            put(28, new Team(28, "Washington Football Team"));
            put(29, new Team(29, "Carolina Panthers"));
            put(30, new Team(30, "Jacksonville Jaguars"));
            put(33, new Team(33, "Baltimore Ravens"));
            put(34, new Team(34, "Houston Texans"));
        }};
        this.currentWeekGames = new ArrayList<>();
        this.scheduleService = new ScheduleService(this);
        scheduleService.updateSchedule();
    }

    public void addGame(int team1Id, int team2Id, ZonedDateTime gameTime) {
        try {
            Team team1  = teamList.get(team1Id);
            Team team2 = teamList.get(team2Id);
            currentWeekGames.add(new Game(team1, team2, gameTime));
        } catch (Exception e) {
            System.out.println("Error adding game");
            System.out.println(e.getMessage());
        }
    }
}
