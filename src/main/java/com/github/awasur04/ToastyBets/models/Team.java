package com.github.awasur04.ToastyBets.models;

public class Team {
    private int id;
    private String name;
    private String abbreviation;
    private float odds;
    private int score;

    public Team(int id, int odds, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.odds = odds;
        this.score = 0;
        this.abbreviation = abbreviation;
    }
    public Team(int id, String name, String abbreviation) {
        this(id,1,name, abbreviation);
    }

    public int getId() {
        return id;
    }

    public float getOdds() {
        return odds;
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

    public void setOdds(float odds) {
        this.odds = odds;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

}
