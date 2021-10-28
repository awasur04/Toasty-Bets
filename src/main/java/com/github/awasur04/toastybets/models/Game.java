package com.github.awasur04.toastybets.models;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game implements Comparable {
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
        return team1.getName() + " (" + team1.getAbbreviation() + ") VS (" + team2.getAbbreviation() + ") " + team2.getName();
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        Game game = (Game)o;
        if (this.gameTime.isBefore(game.getGameTime())) {
            return -1;
        } else if (this.gameTime.isAfter(game.getGameTime())) {
            return 1;
        } else {
            return 0;
        }
    }
}

