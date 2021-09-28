package com.github.awasur04.toastybets.models;

import java.time.ZonedDateTime;

public class Game {
    private Team[] teams;
    private ZonedDateTime gameTime;

    public Game(int id1, int id2, int payout1, int payout2, ZonedDateTime gameTime) {
        Team team1 = new Team(id1, payout1, 0);
        Team team2 = new Team(id2, payout2, 0);
        this.teams = new Team[] {team1, team2};
        this.gameTime = gameTime;
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
}

