package com.github.awasur04.toastybets.models;

public class Team {
    private int id;
    private String name;
    private int payout;
    private int score;

    public Team(int id, int payout, String name) {
        this.id = id;
        this.name = name;
        this.payout = payout;
        this.score = 0;
    }
    public Team(int id, String name) {
        this(id,0,name);
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

    public String getName() {
        return name;
    }

}
