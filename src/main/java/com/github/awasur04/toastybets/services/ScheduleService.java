package com.github.awasur04.toastybets.services;

import com.github.awasur04.toastybets.managers.GameManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ScheduleService {
    private String scheduleURL = "http://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard";
    private GameManager gm;

    public ScheduleService(GameManager gm) {
        this.gm = gm;
    }

    public void updateSchedule() {
        try {
            URL url = new URL(scheduleURL);
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

                JSONParser parser = new JSONParser();
                JSONObject dataObject = (JSONObject) parser.parse(currentLine);

                JSONArray events = (JSONArray) dataObject.get("events");
                for (Object game : events) {
                    JSONObject gameObject = (JSONObject) game;
                    JSONArray competitions = (JSONArray)gameObject.get("competitions");
                    JSONObject teamList = (JSONObject)competitions.get(0);
                    JSONArray competitors = (JSONArray) teamList.get("competitors");
                    JSONObject team1 = (JSONObject)competitors.get(0);
                    JSONObject team2 = (JSONObject)competitors.get(1);
                    System.out.println("ID: " + team1.get("id") + "VS ID:" + team2.get("id"));


                }

            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
