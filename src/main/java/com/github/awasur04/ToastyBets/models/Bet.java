package com.github.awasur04.ToastyBets.models;

import com.github.awasur04.ToastyBets.models.enums.BetStatus;

import javax.persistence.*;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int betId;
    @Column(nullable = false)
    private String discordId;
    @Column(nullable = false)
    private int weekNumber;
    @Column(nullable = false)
    private Long gameId;
    @Column(nullable = false)
    private int teamId;
    @Column(nullable = false)
    private float betAmount;
    @Column(nullable = false)
    private float betOdds;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BetStatus betStatus;

    public int getBetId() {
        return betId;
    }

    public BetStatus getBetStatus() {
        return betStatus;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public float getBetAmount() {
        return betAmount;
    }

    public int getTeamId() {
        return teamId;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public float getBetOdds() {
        return betOdds;
    }

    public float getPayout() {
        return this.betOdds * this.betAmount;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;

    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setBetId(int betId) {
        this.betId = betId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setBetAmount(float betAmount) {
        this.betAmount = betAmount;
    }

    public void setBettingTeam(Team betTeam) {

        this.teamId = betTeam.getId();
    }

    public void setBetOdds(float betOdds) {
        this.betOdds = betOdds;
    }

    public void setBetStatus(BetStatus betStatus) {
        this.betStatus = betStatus;
    }
}
