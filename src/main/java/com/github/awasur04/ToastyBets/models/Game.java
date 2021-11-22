package com.github.awasur04.ToastyBets.models;

import com.github.awasur04.ToastyBets.models.enums.GameStatus;
import org.jetbrains.annotations.NotNull;


import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Game implements Comparable {
    private long matchId;
    private Team team1;
    private Team team2;
    private ZonedDateTime gameTime;
    private GameStatus gameStatus;
    private ArrayList<Integer> team1BetIds;
    private ArrayList<Integer> team2BetIds;

    public Game(long matchId, Team team1, Team team2, ZonedDateTime gameTime) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.gameTime = gameTime;
        this.gameStatus = GameStatus.SCHEDULED;
        team1BetIds = new ArrayList<>();
        team2BetIds = new ArrayList<>();
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ZonedDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(ZonedDateTime gameTime) {
        this.gameTime = gameTime;
    }


    public boolean teamExists(Team team) {
        if (team1.equals(team) || team2.equals(team)) {
            return true;
        }
        return false;
    }

    public long getMatchId() {
        return matchId;
    }

    public ArrayList<Integer> getTeam1BetIds() {
        return team1BetIds;
    }

    public ArrayList<Integer> getTeam2BetIds() {
        return team2BetIds;
    }

    public void addBet(Team team, int betId) {
        if (team.equals(this.team1)) {
            this.team1BetIds.add(betId);
        } else if (team.equals(this.team2)) {
            this.team2BetIds.add(betId);
        }
    }

    public void removeBet(Integer betId) {
        team1BetIds.remove(betId);
        team2BetIds.remove(betId);
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

    public Team getLoser() {
        if (gameStatus == GameStatus.COMPLETED) {
            if (team1.getScore() > team2.getScore()) {
                return team2;
            } else if (team1.getScore() < team2.getScore()){
                return team1;
            } else {
                return null;
            }
        }
        return null;
    }

    public Team getWinner() {
        if (gameStatus == GameStatus.COMPLETED) {
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

    public ArrayList<Integer> getWinningBetIds() {
        Team winningTeam = getWinner();
        if (winningTeam != null) {
            if (getWinner().equals(team1)) {
                return team1BetIds;
            } else if (getWinner().equals(team2)) {
                return team2BetIds;
            }
        }
        return null;
    }

    public ArrayList<Integer> getLosingBetIds() {
        Team winningTeam = getWinner();
        if (winningTeam != null) {
            if (getWinner().equals(team1)) {
                return team2BetIds;
            } else if (getWinner().equals(team2)) {
                return team1BetIds;
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

