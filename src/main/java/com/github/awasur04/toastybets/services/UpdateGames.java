package com.github.awasur04.toastybets.services;

import com.github.awasur04.toastybets.managers.GameManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

public class UpdateGames {
    private String scheduleURL = "http://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard";
    private GameManager gm;

    public UpdateGames(GameManager gm) {
        this.gm = gm;
    }

    public void updateSchedule() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject dataObject = (JSONObject) parser.parse(retrieveJson(scheduleURL));

            JSONArray events = (JSONArray) dataObject.get("events");
            for (Object game : events) {
                JSONObject gameObject = (JSONObject) game;
                JSONArray competitions = (JSONArray) gameObject.get("competitions");
                JSONObject teamList = (JSONObject) competitions.get(0);
                JSONArray competitors = (JSONArray) teamList.get("competitors");
                JSONObject team1 = (JSONObject) competitors.get(0);
                JSONObject team2 = (JSONObject) competitors.get(1);
                Long matchId = Long.parseLong(gameObject.get("id").toString());
                String rawGameDate = gameObject.get("date").toString();
                LocalDate gameDate = LocalDate.of(Integer.valueOf(rawGameDate.substring(0, 4)), Integer.valueOf(rawGameDate.substring(5, 7)), Integer.valueOf(rawGameDate.substring(8, 10)));
                LocalTime gameTime = LocalTime.of(Integer.valueOf(rawGameDate.substring(11, 13)), Integer.valueOf(rawGameDate.substring(14, 16)), 00);
                gm.addGame(matchId, Integer.valueOf(team1.get("id").toString()), Integer.valueOf(team2.get("id").toString()), ZonedDateTime.of(gameDate, gameTime, ZoneId.of("UTC+0")));
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateScore() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject dataObject = (JSONObject) parser.parse(retrieveJson(scheduleURL));

            JSONArray events = (JSONArray) dataObject.get("events");
            for (Object game : events) {
                JSONObject gameObject = (JSONObject) game;
                JSONArray competitions = (JSONArray) gameObject.get("competitions");
                JSONObject teamList = (JSONObject) competitions.get(0);
                JSONArray competitors = (JSONArray) teamList.get("competitors");
                JSONObject team1 = (JSONObject) competitors.get(0);
                JSONObject team2 = (JSONObject) competitors.get(1);
                long matchId = Long.parseLong(gameObject.get("id").toString());
                gm.updateScores(matchId, Integer.valueOf(team1.get("score").toString()), Integer.valueOf(team2.get("score").toString()));
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String retrieveJson(String apiURL) {
        try {
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HTTPResponseCode: " + responseCode);
            } else {
                String currentLine = "";
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    currentLine += scanner.nextLine();
                }
                scanner.close();
                connection.disconnect();
                return currentLine;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
