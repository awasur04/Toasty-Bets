package com.github.awasur04.toastybets.models;

public class Team {
    private int id;
    private String name;
    private String abbreviation;
    private float payout;
    private int score;

    public Team(int id, int payout, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.payout = payout;
        this.score = 0;
        this.abbreviation = abbreviation;
    }
    public Team(int id, String name, String abbreviation) {
        this(id,0,name, abbreviation);
    }

    public int getId() {
        return id;
    }

    public float getPayout() {
        return payout;
    }

    public int getScore() {
        return score;
    }

    public String getAbbreviation() {
        return abbreviation;
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
