package com.github.awasur04.ToastyBets.models;

import com.github.awasur04.ToastyBets.models.enums.PermissionLevel;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column(nullable = false)
    private String discordId;

    @Column(nullable = false)
    private String discordName;

    @Column(nullable = false, columnDefinition = "integer default 1000")
    private int toastyCoins;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionLevel permissionLevel;

    @Column(nullable = false)
    private String timeZone;

    //private ArrayList<Bet> currentBets;


    public String getDiscordName() {
        return discordName;
    }

    public String getDiscordId() {
        return discordId;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public int getToastyCoins() {
        return toastyCoins;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setPermissionLevel(PermissionLevel permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public void setToastyCoins(int toastyCoins) {
        this.toastyCoins = toastyCoins;
    }
}
