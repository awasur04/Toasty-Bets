package com.github.awasur04.ToastyBets.update;

import com.github.awasur04.ToastyBets.game.GameManager;
import com.github.awasur04.ToastyBets.models.Game;
import com.github.awasur04.ToastyBets.models.Team;
import com.github.awasur04.ToastyBets.models.TeamList;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

@Component
public class UpdateGames {

    @Value("${nfl.score.update}")
    private String scheduleURL;

    @Autowired
    private GameManager gameManager;

    public void updateSchedule() {
        try {
            LogManager.log("Updating weekly schedule");
            JSONParser parser = new JSONParser();
            JSONObject dataObject = (JSONObject) parser.parse(retrieveJson(scheduleURL));


            JSONObject weekNumber = (JSONObject)dataObject.get("week");
            gameManager.setWeekNumber(Integer.valueOf(weekNumber.get("number").toString()));

            JSONArray events = (JSONArray) dataObject.get("events");
            for (Object game : events) {
                JSONObject gameObject = (JSONObject) game;

                Long matchId = Long.parseLong(gameObject.get("id").toString());

                String rawGameDate = gameObject.get("date").toString();

                JSONArray competitions = (JSONArray)gameObject.get("competitions");
                JSONObject match0 = (JSONObject)competitions.get(0);
                JSONArray competitors = (JSONArray)match0.get("competitors");
                JSONObject homeTeam = (JSONObject)competitors.get(0);
                JSONObject awayTeam = (JSONObject)competitors.get(1);

                int team1Id = Integer.valueOf(homeTeam.get("id").toString());
                int team2Id = Integer.valueOf(awayTeam.get("id").toString());
                Team team1 = TeamList.teamList.get(team1Id);
                Team team2 = TeamList.teamList.get(team2Id);

                LocalDate gameDate = LocalDate.of(Integer.valueOf(rawGameDate.substring(0, 4)), Integer.valueOf(rawGameDate.substring(5, 7)), Integer.valueOf(rawGameDate.substring(8, 10)));
                LocalTime gameTime = LocalTime.of(Integer.valueOf(rawGameDate.substring(11, 13)), Integer.valueOf(rawGameDate.substring(14, 16)), 00);

                gameManager.addGame(matchId, new Game(team1, team2, ZonedDateTime.of(gameDate, gameTime, ZoneId.of("Europe/London"))));
            }
        }catch (Exception e) {
            LogManager.error("Failed to update game schedule ",e.getMessage());
        }
    }

//    public void updateScore() {
//        try {
//            LogManager.log("Updating Scores");
//            JSONParser parser = new JSONParser();
//            JSONObject dataObject = (JSONObject) parser.parse(retrieveJson(scheduleURL));
//
//            JSONArray events = (JSONArray) dataObject.get("events");
//            for (Object game : events) {
//                JSONObject gameObject = (JSONObject) game;
//
//                //Retrieve team scores
//                JSONArray competitions = (JSONArray) gameObject.get("competitions");
//                JSONObject teamList = (JSONObject) competitions.get(0);
//                JSONArray competitors = (JSONArray) teamList.get("competitors");
//                JSONObject team1 = (JSONObject) competitors.get(0);
//                JSONObject team2 = (JSONObject) competitors.get(1);
//                long matchId = Long.parseLong(gameObject.get("id").toString());
//
//                JSONObject statuses = (JSONObject)gameObject.get("status");
//                JSONObject statusType = (JSONObject)statuses.get("type");
//                boolean statusBoolean = Boolean.parseBoolean(statusType.get("completed").toString());
//
//                gm.updateScores(matchId, Integer.valueOf(team1.get("score").toString()), Integer.valueOf(team2.get("score").toString()), statusBoolean);
//            }
//        }catch (Exception e) {
//            System.out.println(e.getStackTrace().toString());
//        }
//    }

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
            LogManager.error("Failed to retrieve JSON file ", e.getMessage());
        }
        return "";
    }
}
