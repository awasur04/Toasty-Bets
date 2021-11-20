package com.github.awasur04.ToastyBets.game;

import com.github.awasur04.ToastyBets.database.DatabaseService;
import com.github.awasur04.ToastyBets.discord.ResponseHandler;
import com.github.awasur04.ToastyBets.models.Bet;
import com.github.awasur04.ToastyBets.models.Game;
import com.github.awasur04.ToastyBets.models.Team;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.BetStatus;
import com.github.awasur04.ToastyBets.models.enums.GameStatus;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import com.github.awasur04.ToastyBets.utilities.TeamList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameManager {
    private HashMap<Long, Game> weekSchedule = new HashMap<>();
    private int weekNumber;
    private static DatabaseService databaseService;
    private static ResponseHandler responseHandler;

    @Autowired
    public void setResponseHandler(ResponseHandler responseHandler) {
        GameManager.responseHandler = responseHandler;
    }

    @Autowired
    public void setDatabaseService(DatabaseService databaseService) {
        GameManager.databaseService = databaseService;
    }

    public ArrayList<Game> getWeekSchedule() {
        ArrayList<Game> temp = new ArrayList<Game>();
        for(Game currentGame : this.weekSchedule.values()) {
            temp.add(currentGame);
        }
        Collections.sort(temp);
        return temp;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public Game getGame(Team team) {
        for (Game game : weekSchedule.values()) {
            if (game.teamExists(team)) {
                return game;
            }
        }
        return null;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void addGame(Long matchId, Game game) {
        try {
            if (game != null && matchId != null) {
                this.weekSchedule.put(matchId, game);
            }
        }catch (Exception e) {
            LogManager.error("Failed to add a game to weekly schedule", e.getMessage());
        }

    }

    public void createNewBet(User source, Team betTeam, int betAmount) {
        try {
            Game betGame = getGame(betTeam);
            if (betGame == null) {
                throw new NullPointerException("Unable to place bet: Invalid team abbreviation");
            }
            float newCoinBalance = source.getToastyCoins() - betAmount;
            source.setToastyCoins(newCoinBalance);
            databaseService.updateUser(source);

            Bet newBet = new Bet();
            newBet.setBetAmount(betAmount);
            newBet.setDiscordId(source.getDiscordId());
            newBet.setGameId(betGame.getMatchId());
            newBet.setWeekNumber(weekNumber);
            newBet.setBetStatus(BetStatus.ACTIVE);
            newBet.setBetOdds(betTeam.getOdds());
            newBet.setBettingTeam(betTeam);
            databaseService.saveNewBet(newBet);

            betGame.addBet(betTeam, newBet.getBetId());
            //UPDATE CURRENT BET MESSAGE
        } catch (Exception e) {
            LogManager.error("Failed to create new bet ", e.getMessage());
        }
    }

    public void updateGameScore(Long matchId, int team1Score, int team2Score, String matchState) {

        try {
            Game currentGame = weekSchedule.get(matchId);
            currentGame.getTeam(1).setScore(team1Score);
            currentGame.getTeam(2).setScore(team2Score);

            switch(matchState) {
                case "STATUS_SCHEDULED":
                    currentGame.setGameStatus(GameStatus.SCHEDULED);
                    break;
                //case "STATUS_IP": currentGame.setGameStatus(GameStatus.IN_PROGRESS); break;
                case "STATUS_FINAL":
                    currentGame.setGameStatus(GameStatus.COMPLETED);
                    payoutCompletedGame(matchId);
                    break;
            }
        }catch(Exception e) {
            LogManager.error("Failed to update game scores", e.getMessage());
        }

    }

    public void updateTeamOdds(String teamName, float odds) {
        Team updateTeam = TeamList.getTeamByName(teamName);
        if (updateTeam == null) {
            return;
        }
        updateTeam.setOdds(odds);
    }

    public void payoutCompletedGame(long matchId) {
        int totalPayouts = 0;
        try {
            Game currentGame = weekSchedule.get(matchId);
            if (currentGame.getGameStatus() == GameStatus.COMPLETED) {
                ArrayList<Integer> winners = currentGame.getWinningBetIds();
                for (Iterator<Integer> iterator = winners.iterator(); iterator.hasNext();) {
                    Bet currentBet = databaseService.findBet(iterator.next());
                    User currentUser = databaseService.findUser(currentBet.getDiscordId());
                    float newBalance = currentUser.getToastyCoins() + currentBet.getPayout();
                    if (newBalance >= 0 && currentBet.getBetStatus() == BetStatus.ACTIVE) {
                        currentUser.setToastyCoins(newBalance);
                        currentBet.setBetStatus(BetStatus.ARCHIVED);
                        iterator.remove();

                        responseHandler.displayPayout(currentUser, currentBet);

                        databaseService.updateBet(currentBet);
                        databaseService.updateUser(currentUser);

                        totalPayouts++;
                    }
                }
            }
        }catch(Exception e) {
            LogManager.error("Failed to payout completed game ", e.getMessage());
            e.printStackTrace();
        }finally {
            LogManager.log("Completed payouts: " + totalPayouts + " payouts completed");
        }
    }

    public void registerActiveBets() {
        try {
            List<Bet> activeBets = databaseService.findActiveBets();
            for(Bet bet : activeBets) {
                Game betGame = weekSchedule.get(bet.getGameId());
                Team betTeam = TeamList.teamList.get(bet.getTeamId());
                betGame.addBet(betTeam, bet.getBetId());
            }
        }catch(Exception e) {
            LogManager.error("Unable to register active bets", e.getMessage());
        }
    }
}
