package com.github.awasur04.toastybets.models;

import java.time.ZonedDateTime;

public class Game {
    private Team[] teams;
    private ZonedDateTime gameTime;
    private boolean gameCompleted;

    public Game(Team team1, Team team2, ZonedDateTime gameTime) {
        this.teams = new Team[] {team1, team2};
        this.gameTime = gameTime;
        this.gameCompleted = false;
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public ZonedDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(ZonedDateTime gameTime) {
        this.gameTime = gameTime;
    }

    public Team getWinner() {
        if (gameCompleted) {
            if (teams[0].getScore() > teams[1].getScore()) {
                return teams[0];
            } else if (teams[0].getScore() < teams[1].getScore()){
                return teams[1];
            } else {
                return null;
            }
        }
        return null;
    }
}

