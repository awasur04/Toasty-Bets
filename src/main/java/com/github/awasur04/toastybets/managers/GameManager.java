package com.github.awasur04.toastybets.managers;

import com.github.awasur04.toastybets.database.DatabaseManager;
import com.github.awasur04.toastybets.discord.DiscordManager;
import com.github.awasur04.toastybets.models.Game;
import com.github.awasur04.toastybets.models.Team;
import com.github.awasur04.toastybets.services.UpdateGames;

import java.time.ZonedDateTime;
import java.util.*;

public class GameManager {
    private Map<Integer, Team> teamList;
    private DatabaseManager databaseManager;
    private HashMap<Long, Game> currentWeekGames;
    private UpdateGames update;
    private ScheduledEventsManager eventsManager;
    private DiscordManager discordManager;
    private int currentWeek;

    public GameManager(String token) {
        this.teamList = new HashMap<>() {{
            put(1, new Team(1,"Atlanta Falcons", "ATL"));
            put(2, new Team(2, "Buffalo Bills", "BUF"));
            put(3, new Team(3, "Chicago Bears", "CHI"));
            put(4, new Team(4, "Cincinnati Bengals", "CIN"));
            put(5, new Team(5, "Cleveland Browns", "CLE"));
            put(6, new Team(6, "Dallas Cowboys", "DAL"));
            put(7, new Team(7, "Denver Broncos", "DEN"));
            put(8, new Team(8, "Detroit Lions", "DET"));
            put(9, new Team(9, "Green Bay Packers", "GB"));
            put(10, new Team(10, "Tennessee Titans", "TEN"));
            put(11, new Team(11, "Indianapolis Colts", "IND"));
            put(12, new Team(12, "Kansas City Chiefs", "KC"));
            put(13, new Team(13, "Las Vegas Raiders", "LV"));
            put(14, new Team(14, "Las Angeles Rams", "LAR"));
            put(15, new Team(15, "Miami Dolphins", "MIA"));
            put(16, new Team(16, "Minnesota Vikings", "MIN"));
            put(17, new Team(17, "New England Patriots", "NE"));
            put(18, new Team(18, "New Orleans Saints", "NO"));
            put(19, new Team(19, "New York Giants", "NYG"));
            put(20, new Team(20, "New York Jets", "NYJ"));
            put(21, new Team(21, "Philadelphia Eagles", "PHI"));
            put(22, new Team(22, "Arizona Cardinals", "ARI"));
            put(23, new Team(23, "Pittsburgh Steelers", "PIT"));
            put(24, new Team(24, "Los Angeles Chargers", "LAC"));
            put(25, new Team(25, "San Francisco 49ers", "SF"));
            put(26, new Team(26, "Seattle Seahawks", "SEA"));
            put(27, new Team(27, "Tampa Bay Buccaneers", "TB"));
            put(28, new Team(28, "Washington Football Team", "WAS"));
            put(29, new Team(29, "Carolina Panthers", "CAR"));
            put(30, new Team(30, "Jacksonville Jaguars", "JAX"));
            put(33, new Team(33, "Baltimore Ravens", "BAL"));
            put(34, new Team(34, "Houston Texans", "HOU"));
        }};
        this.currentWeekGames = new HashMap<>();




        this.update = new UpdateGames(this);
        this.eventsManager = new ScheduledEventsManager();
        this.databaseManager = new DatabaseManager();
        this.discordManager = new DiscordManager(token, this);
        LogManager.log("Program starting");
        update.updateSchedule();
        scheduleEvents();
    }


    ///update schedule, update score, update odds
    private void scheduleEvents() {
        eventsManager.addEvent(this::updateSchedule, ScheduledEventsManager.UpdateFrequency.WEEKLY); //Update our schedule weekly
        eventsManager.addEvent(update::updateScore, ScheduledEventsManager.UpdateFrequency.NFLGAMEDAY); //Update score on sunday
        eventsManager.addEvent(update::updateScore, ScheduledEventsManager.UpdateFrequency.THURSDAYNIGHT); //Update score on thursday games
        eventsManager.addEvent(update::updateScore, ScheduledEventsManager.UpdateFrequency.MONDAYNIGHT); //Update score on monday night
    }
    public void closeProgram() {
        LogManager.log("Program shutting down");
        eventsManager.shutdownExecutor();
        LogManager.closeLogs();
    }

    public void addGame(long gameId, int team1Id, int team2Id, ZonedDateTime gameTime) {
        try {
            Team team1  = teamList.get(team1Id);
            Team team2 = teamList.get(team2Id);
            currentWeekGames.put(gameId, new Game(team1, team2, gameTime));
        } catch (Exception e) {
            System.out.println("Error adding game");
            System.out.println(e.getMessage());
        }
    }

    public void updateWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public void updateSchedule() {
        this.currentWeekGames.clear();
        eventsManager.resetExecutor();
        update.updateSchedule();
        scheduleEvents();
    }

    public HashMap<Long, Game> getCurrentWeekGames() {
        return currentWeekGames;
    }
    public int getCurrentWeek() {
        return currentWeek;
    }

    public ArrayList<Game> getGameList() {
        ArrayList<Game> temp = new ArrayList<Game>();
        for(Game currentGame : this.currentWeekGames.values()) {
            temp.add(currentGame);
        }
        Collections.sort(temp);
        return temp;
    }

    public void updateScores(long matchId, int team1score, int team2score) {
        Game currentGame = currentWeekGames.get(matchId);
        currentGame.getTeams().get(0).setScore(team1score);
        currentGame.getTeams().get(1).setScore(team2score);
    }

    public void printGames() {
        for (Game game : currentWeekGames.values()) {
            System.out.println("\n--------------------------");
            System.out.println(game);
            System.out.println("--------------------------");
        }
    }

    public DatabaseManager getDB() {
        return this.databaseManager;
    }

}
