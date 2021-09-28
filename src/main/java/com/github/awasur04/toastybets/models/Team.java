package com.github.awasur04.toastybets.models;

public class Team {
    private int id;
    private int payout;
    private int score;

    public Team(int id, int payout, int score) {
        this.id = id;
        this.payout = payout;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public int getPayout() {
        return payout;
    }

    public int getScore() {
        return score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPayout(int payout) {
        this.payout = payout;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
