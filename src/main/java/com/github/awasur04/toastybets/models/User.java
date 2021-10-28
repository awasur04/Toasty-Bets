package com.github.awasur04.toastybets.models;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

public class User {
    private Long discordId;
    private String discordName;
    private ArrayList<Bet> currentBets;
    private int toastyCoins;
    private Permission_Level permissionLevel;
    private String timeZone;

    static enum Permission_Level {
        DEV,
        ADMIN,
        NORMAL
    }

    public User(Long id, String name, int coins, String permissionLevel, String inputTimezone) {
        this.discordId = id;
        this.discordName = name;
        this.toastyCoins = coins;
        this.currentBets = new ArrayList<>();
        this.permissionLevel = Permission_Level.valueOf(permissionLevel);

    public Permission_Level getPermissionLevel() {
        return permissionLevel;
    }

    }

    public int getToastyCoins() {
        return toastyCoins;
    }

    public void setPermissionLevel(Permission_Level permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Long getDiscordId() {
        return discordId;
    }

    public String getDiscordName() {
        return discordName;
    }
    public void addToastyCoins(int value) {
        if (value >= 0) {
            this.toastyCoins = this.toastyCoins + value;
        }
    }

    public void removeToastyCoins(int value) {
        if (value >= 0) {
            this.toastyCoins = this.toastyCoins - value;
        }
    }

    public void addBet(Bet bet) {
        try {
            this.currentBets.add(bet);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removeBet(int index) {
        try {
            this.currentBets.remove(index);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<Bet> getCurrentBets() {
        return currentBets;
    }

    public int getBetCount() {
        return this.currentBets.size();
    }

    @Override
    public String toString() {
        return this.discordId + " + " + this.discordName + " + " + this.toastyCoins;
    }
}
