package com.github.awasur04.toastybets.models;

public class Bet {
    private User bettingUser;
    private Game bettingGame;
    private Team bettingTeam;
    private int bettingAmount;

    public Bet(User user, int betAmount, Game betGame, Team betTeam) {
        this.bettingUser = user;
        this.bettingAmount = betAmount;
        this.bettingGame = betGame;
        this.bettingTeam = betTeam;
    }

    public double getPayout() {
        return this.bettingTeam.getPayout() * this.bettingAmount;
    }

    public void setBetAmount(int betAmount) {
        this.bettingAmount = betAmount;
    }

    public void setBettingTeam(Team betTeam) {
        this.bettingTeam = betTeam;
    }
}
