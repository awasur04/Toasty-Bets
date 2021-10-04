package com.github.awasur04.toastybets.models;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Team team1;
    private Team team2;
    private ZonedDateTime gameTime;
    private boolean gameCompleted;

    public Game(Team team1, Team team2, ZonedDateTime gameTime) {
        this.team1 = team1;
        this.team2 = team2;
        this.gameTime = gameTime;
        this.gameCompleted = false;
    }

    public ArrayList<Team> getTeams() {
        return new ArrayList<>(List.of(team1, team2));
    }

    public ZonedDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(ZonedDateTime gameTime) {
        this.gameTime = gameTime;
    }

    public Team getWinner() {
        if (gameCompleted) {
            if (team1.getScore() > team2.getScore()) {
                return team1;
            } else if (team1.getScore() < team2.getScore()){
                return team2;
            } else {
                return null;
            }
        }
        return null;
    }

    public String toString() {
        return team1.getName() + "(" + team1.getAbbreviation() + ") VS " + team2.getName() + "(" + team2.getAbbreviation() + ")" +
                "\nDate: " + gameTime.withZoneSameInstant(ZoneId.of("UTC-5")).toLocalDateTime();
    }
}

