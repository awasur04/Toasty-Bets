package com.github.awasur04.ToastyBets.models;

import com.github.awasur04.ToastyBets.models.Team;

import java.util.HashMap;

public class TeamList {
    public static final HashMap<Integer, Team> teamList = new HashMap<>() {{
        put(1, new Team(1,"Atlanta Falcons", "ATL"));
        put(2, new Team(2, "Buffalo Bills", "BUF"));
        put(3, new Team(3, "Chicago Bears", "CHI"));
        put(4, new Team(4, "Cincinnati Bengals", "CIN"));
        put(5, new Team(5, "Cleveland Browns", "CLE"));
        put(6, new Team(6, "Dallas Cowboys", "DAL"));
        put(7, new Team(7, "Denver Broncos", "DEN"));
        put(8, new Team(8, "Detroit Lions", "DET"));
        put(9, new Team(9, "Green Bay Packers", "GB"));
        put(10, new Team(10, "Tennessee Titans", "TEN"));
        put(11, new Team(11, "Indianapolis Colts", "IND"));
        put(12, new Team(12, "Kansas City Chiefs", "KC"));
        put(13, new Team(13, "Las Vegas Raiders", "LV"));
        put(14, new Team(14, "Los Angeles Rams", "LAR"));
        put(15, new Team(15, "Miami Dolphins", "MIA"));
        put(16, new Team(16, "Minnesota Vikings", "MIN"));
        put(17, new Team(17, "New England Patriots", "NE"));
        put(18, new Team(18, "New Orleans Saints", "NO"));
        put(19, new Team(19, "New York Giants", "NYG"));
        put(20, new Team(20, "New York Jets", "NYJ"));
        put(21, new Team(21, "Philadelphia Eagles", "PHI"));
        put(22, new Team(22, "Arizona Cardinals", "ARI"));
        put(23, new Team(23, "Pittsburgh Steelers", "PIT"));
        put(24, new Team(24, "Los Angeles Chargers", "LAC"));
        put(25, new Team(25, "San Francisco 49ers", "SF"));
        put(26, new Team(26, "Seattle Seahawks", "SEA"));
        put(27, new Team(27, "Tampa Bay Buccaneers", "TB"));
        put(28, new Team(28, "Washington Football Team", "WAS"));
        put(29, new Team(29, "Carolina Panthers", "CAR"));
        put(30, new Team(30, "Jacksonville Jaguars", "JAX"));
        put(33, new Team(33, "Baltimore Ravens", "BAL"));
        put(34, new Team(34, "Houston Texans", "HOU"));
    }};
}
