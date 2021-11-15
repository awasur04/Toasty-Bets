package com.github.awasur04.toastybets.services;

import com.github.awasur04.toastybets.game.BetManager;
import com.github.awasur04.toastybets.utilities.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

public class UpdateOdds {
    private String oddsURL = "";
    private BetManager bm;

    public UpdateOdds(BetManager bm) {
        this.bm = bm;
        initialize();
    }

    public void initialize() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            this.oddsURL = properties.getProperty("odds.url");
        }catch (IOException e) {
            LogManager.error("Failed to locate properties file", e.getMessage());
        }catch (Exception e) {
            LogManager.error("Betting Manager failed to initialize", e.getMessage());
        }
    }

    public void updateOdds() {
        try {
            LogManager.log("Updating Game Odds");
            JSONParser parser = new JSONParser();
            JSONArray dataObject = (JSONArray) parser.parse(retrieveJson(oddsURL));

            for (Object gameObject : dataObject) {
                JSONObject currentGame = (JSONObject) gameObject;
                JSONArray bookmakers = (JSONArray) currentGame.get("bookmakers");
                for (Object bookSiteObject : bookmakers) {
                    JSONObject bookSite = (JSONObject) bookSiteObject;
                    if (bookSite.get("key").equals("fanduel")) {

                        JSONArray markets = (JSONArray) bookSite.get("markets");
                        JSONObject gameOdds = (JSONObject) markets.get(0);
                        JSONArray outcome = (JSONArray) gameOdds.get("outcomes");

                        for (Object teamObject : outcome) {
                            JSONObject team = (JSONObject) teamObject;
                            String name = team.get("name").toString();
                            String odds = team.get("price").toString();
                            bm.updateTeamOdds(name, odds);
                        }
                    }
                }
            }
        }catch (Exception e) {
            LogManager.error("Update Odds: ",e.getMessage());
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
