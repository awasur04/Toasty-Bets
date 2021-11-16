package com.github.awasur04.ToastyBets.models;

import org.jetbrains.annotations.NotNull;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game implements Comparable {
    private Team team1;
    private Team team2;
    private ZonedDateTime gameTime;
    private boolean gameCompleted;
    private boolean gameStarted;

    public Game(Team team1, Team team2, ZonedDateTime gameTime) {
        this.team1 = team1;
        this.team2 = team2;
        this.gameTime = gameTime;
        this.gameCompleted = false;
        this.gameStarted = false;
    }

    public ZonedDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(ZonedDateTime gameTime) {
        this.gameTime = gameTime;
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameStatus) {
        this.gameCompleted = gameStatus;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Team getTeam(int teamNumber) {
        switch(teamNumber){
            case 1:
                return team1;
            case 2:
                return team2;
            default:
                return null;
        }
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

    @Override
    public String toString() {
        return team1.getName() + " (" + team1.getAbbreviation() + ") VS (" + team2.getAbbreviation() + ") " + team2.getName();
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

