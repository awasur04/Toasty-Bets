package com.github.awasur04.toastybets.discord;

import com.github.awasur04.toastybets.models.Team;
import com.github.awasur04.toastybets.utilities.LogManager;
import com.github.awasur04.toastybets.models.Game;
import com.github.awasur04.toastybets.models.User;
import com.github.awasur04.toastybets.utilities.DateFormat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ResponseHandler {
    private JDA toastyBot;

    public static HashMap<String, String> emojiValues = new HashMap<String, String>() {{
        put("ARI", "<:ARI:902972769623490631>");
        put("ATL", "<:ATL:902972769803833384>");
        put("BAL", "<:BAL:902972769917100062>");
        put("BUF", "<:BUF:902972769866776647>");
        put("CAR", "<:CAR:902972770428813322>");
        put("CHI", "<:CHI:902972770093256764>");
        put("CIN", "<:CIN:902972769606717462>");
        put("CLE", "<:CLE:902972770017751070>");
        put("DAL", "<:DAL:902972770030350336>");
        put("DEN", "<:DEN:902972770017771630>");
        put("DET", "<:DET:902972770164539432>");
        put("GB", "<:GB:902972770126823424>");
        put("HOU", "<:HOU:902972770034536508>");
        put("IND", "<:IND:902972769770287126>");
        put("JAX", "<:JAX:902972770042929162>");
        put("KC", "<:KC:902972770193928192>");
        put("LAC", "<:LAC:902972659262951445>");
        put("LAR", "<:LAR:902972659262951445>");
        put("LV", "<:LV:902972770122620948>");
        put("MIA", "<:MIA:902972770210693180>");
        put("MIN", "<:MIN:902972770311344128>");
        put("NE", "<:NE:902972770248441856>");
        put("NO", "<:NO:902972770172940368>");
        put("NYG", "<:NYG:902972769829003317>");
        put("NYJ", "<:NYJ:902972769506050060>");
        put("PHI", "<:PHI:902972770281996288>");
        put("PIT", "<:PIT:902972770047127572>");
        put("SEA", "<:SEA:902972770214879322>");
        put("SF", "<:SF:902972770030325812>");
        put("TB", "<:TB:902972770349117510>");
        put("TEN", "<:TEN:902972770177134652>");
        put("WAS", "<:WAS:902972770294595685>");
    }};


    public ResponseHandler(JDA bot) {
        this.toastyBot = bot;
    }

    public void sendWeeklySchedule(User user, ArrayList<Game> gameList, int weekNumber) {
        try {
            if (!gameList.isEmpty()) {

                Random random = new Random();
                float r = random.nextFloat();
                float g = random.nextFloat();
                float b = random.nextFloat();
                Color weeklyColor = new Color(r,g,b);

                net.dv8tion.jda.api.entities.User discordUser = toastyBot.getUserById(user.getDiscordId());
                if (discordUser != null && !(discordUser.isBot() && discordUser.isSystem())) {
                    MessageEmbed gameMessage = createGameMessage(gameList, weekNumber, weeklyColor, user.getTimeZone());
                    discordUser.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessageEmbeds(gameMessage)).queue();
                }
            }
        }catch (Exception e) {
            LogManager.error("Cannot send weekly schedule to user ID: " + user.getDiscordId(), e.getStackTrace().toString());
        }
    }

    public void newUserSetup(User newUser) {
        net.dv8tion.jda.api.entities.User discordUser = toastyBot.getUserById(newUser.getDiscordId());
        if (discordUser != null && !(discordUser.isBot() && discordUser.isSystem())) {
            discordUser.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessageEmbeds(timeZoneMessage())).queue();
        }
    }

    public void displayHelp(String discordId) {
        net.dv8tion.jda.api.entities.User discordUser = toastyBot.getUserById(discordId);
        if (discordUser != null && !(discordUser.isBot() && discordUser.isSystem())) {
            discordUser.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessageEmbeds(helpMessage())).queue();
        }
    }


    public MessageEmbed helpMessage() {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Welcome to Toasty Bets NFL Betting Bot");
            eb.addField("","Every wednesday you will be sent a new weekly schedule", false);
            eb.addField("","This schedule will contain the teams and current betting rate", false);
            eb.addField("","Once you place a bet at a certain rate that bet is locked in (NO CANCELING)", false);
            eb.addField("","Bets will be paid out at the end of each day a game is played", false);
            eb.addField("","You start with 1000 ToastyCoins, and will receive 200 ToastyCoins each week", false);
            eb.addField("", "Total Payout = Betting Odds * Bet Amount", false);
            eb.setFooter("Comments, questions, or ideas please message me @cool#5783");
            return eb.build();
        } catch (Exception e) {
            LogManager.error("Cannot create help message", e.getStackTrace().toString());
        }
        return null;
    }

    public MessageEmbed timeZoneMessage() {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Please Register your Account");
            eb.setDescription("Supported Time Zones:\nPlease make sure you include the timezone in your command like in the picture");
            eb.setImage("https://i.imgur.com/prrJSno.png");
            eb.addField("EST", "Eastern Standard Time", false);
            eb.addField("CST", "Central Standard Time", false);
            eb.addField("MST", "Mountain Standard Time", false);
            eb.addField("PST", "Pacific Standard Time", false);
            eb.addField("BST", "British Summer Time", false);
            eb.setFooter("Please register your time zone using the command /register <abbreviation>");
            return eb.build();
        } catch (Exception e) {
            LogManager.error("Cannot create weekly message", e.getStackTrace().toString());
        }
        return null;
    }


    public MessageEmbed createGameMessage(ArrayList<Game> gameList, int weekNumber, Color color, String userZoneId) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(color);
            eb.setAuthor("NFL Week: " + weekNumber);
            for (Game currentGame : gameList) {
                Team team1 = currentGame.getTeams().get(0);
                Team team2 = currentGame.getTeams().get(1);
                String title = emojiValues.get(team1.getAbbreviation()) + currentGame.toString() + emojiValues.get(team2.getAbbreviation());
                String gameDate = DateFormat.formatDate(currentGame.getGameTime().withZoneSameInstant(ZoneId.of(userZoneId)).toLocalDateTime());
                if (currentGame.isGameCompleted()) {
                    String gameScore = team1.getScore() + " - " + team2.getScore();
                    eb.addField(title, "Date: " + gameDate + "\nFinal Score: " + gameScore, false);
                } else {
                    eb.addField(title, "Date: " + gameDate + "\nOdds: " + team1.getOdds() + " VS Odds: " + team2.getOdds(), false);
                }
            }
            eb.setFooter("/bet <Team Abbreviation> <Amount>");
            return eb.build();
        } catch (Exception e) {
            LogManager.error("Cannot create weekly message", e.getStackTrace().toString());
        }
        return null;
    }
}
