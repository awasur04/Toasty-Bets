package com.github.awasur04.ToastyBets.game;

import com.github.awasur04.ToastyBets.database.DatabaseService;
import com.github.awasur04.ToastyBets.discord.ResponseHandler;
import com.github.awasur04.ToastyBets.exceptions.GameLockedException;
import com.github.awasur04.ToastyBets.models.Bet;
import com.github.awasur04.ToastyBets.models.Game;
import com.github.awasur04.ToastyBets.models.Team;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.BetStatus;
import com.github.awasur04.ToastyBets.models.enums.GameStatus;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import com.github.awasur04.ToastyBets.utilities.TeamList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class GameManager {
    private HashMap<Long, Game> weekSchedule = new HashMap<>();
    private int weekNumber;
    private static DatabaseService databaseService;
    private static ResponseHandler responseHandler;
    @Value("#{new Integer('${user.default.toastycoin.pay}')}")
    private int weeklyPay;

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

    public void createNewBet(User source, Team betTeam, int betAmount) throws GameLockedException{
        try {
            Game betGame = getGame(betTeam);
            if (betGame == null) {
                throw new NullPointerException("Unable to place bet: Invalid team abbreviation");
            }

            if (betGame.getGameStatus() == GameStatus.IN_PROGRESS || betGame.getGameStatus() == GameStatus.COMPLETED) {
                throw new GameLockedException("Game has already started");
            }

            int newCoinBalance = (int)Math.ceil(source.getToastyCoins() - betAmount);
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
            responseHandler.payoutMessage(source, weekNumber);
        } catch (GameLockedException gm) {
            throw new GameLockedException(gm.getMessage());
        } catch (Exception e) {
            LogManager.error("Failed to create new bet ", e.getMessage());
        }
    }

    public void updateGameScore(Long matchId, int team1Score, int team2Score, String matchState) {

        try {
            Game currentGame = weekSchedule.get(matchId);
            if (currentGame != null) {
                currentGame.getTeam(1).setScore(team1Score);
                currentGame.getTeam(2).setScore(team2Score);

                switch(matchState) {
                    case "STATUS_SCHEDULED":
                        currentGame.setGameStatus(GameStatus.SCHEDULED);
                        break;
                    case "STATUS_IN_PROGRESS": currentGame.setGameStatus(GameStatus.IN_PROGRESS); break;
                    case "STATUS_FINAL":
                        if (currentGame.getGameStatus() != GameStatus.COMPLETED) {
                            currentGame.setGameStatus(GameStatus.COMPLETED);
                            payoutCompletedGame(matchId);
                            removeCompletedLosers(matchId);
                            //weekSchedule.remove(matchId);
                        }
                        break;
                }
            }else {
                throw new RuntimeException("updateGameScore returned a null game");
            }
        }catch(Exception e) {
            LogManager.error("Failed to update game scores", e.getMessage());
        }

    }

    public void clearWeeklyGames() {
        this.weekSchedule = new HashMap<>();
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
                for (Integer betId : winners) {
                    Bet currentBet = databaseService.findBet(betId);
                    User currentUser = databaseService.findUser(currentBet.getDiscordId());
                    int newBalance = (int)Math.ceil(currentUser.getToastyCoins() + currentBet.getPayout());
                    if (newBalance >= 0 && currentBet.getBetStatus() == BetStatus.ACTIVE) {
                        currentUser.setToastyCoins(newBalance);
                        currentBet.setBetStatus(BetStatus.WON);

                        responseHandler.payoutMessage(currentUser, weekNumber);

                        databaseService.updateBet(currentBet);
                        databaseService.updateUser(currentUser);

                        totalPayouts++;
                    }
                }
                currentGame.clearBets(currentGame.getWinner());
            }
        }catch(Exception e) {
            LogManager.error("Failed to payout completed game ", e.getMessage());
            e.printStackTrace();
        }finally {
            LogManager.log("Completed payouts: " + totalPayouts + " payouts completed");
        }
    }

    public void removeCompletedLosers(long matchId) {
        try {
            Game currentGame = weekSchedule.get(matchId);
            if (currentGame.getGameStatus() == GameStatus.COMPLETED) {
                ArrayList<Integer> losers = currentGame.getLosingBetIds();
                for (Integer loserId : losers) {
                    Bet currentBet = databaseService.findBet(loserId);
                    User currentUser = databaseService.findUser(currentBet.getDiscordId());
                    if (currentBet.getBetStatus() == BetStatus.ACTIVE) {
                        currentBet.setBetStatus(BetStatus.LOSS);

                        databaseService.updateBet(currentBet);
                        responseHandler.payoutMessage(currentUser, weekNumber);
                    }
                }
                currentGame.clearBets(currentGame.getLoser());
            }
        } catch (Exception e) {
            LogManager.error("Failed to remove losing bets ", e.getMessage());
        }
    }

    public void registerActiveBets() {
        try {
            List<Bet> activeBets = databaseService.findActiveBets();
            LogManager.log("Active Bets size " + activeBets.size());
            for(Bet bet : activeBets) {
                Game betGame = weekSchedule.get(bet.getGameId());
                Team betTeam = TeamList.teamList.get(bet.getTeamId());
                betGame.addBet(betTeam, bet.getBetId());
            }
        }catch(Exception e) {
            LogManager.error("Unable to register active bets", e.getMessage());
        }
    }

    public void checkGameStart() {
        for (Game game : weekSchedule.values()) {
            if (game.getGameStatus() == GameStatus.SCHEDULED) {
                ZonedDateTime gameTime = game.getGameTime();
                ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of("UTC+0"));
                if (ChronoUnit.MINUTES.between(localTime, gameTime) <= 60) {
                    game.setGameStatus(GameStatus.IN_PROGRESS);
                }
            }
        }
    }

    public List<Bet> getCurrentWeekBets(User source) {
        return databaseService.findCurrentWeekBets(this.weekNumber, source.getDiscordId());
    }

    public void sendAllUsersSchedule() {
        LogManager.log("Sending all users updated schedule");
        List<User> activeUserList = databaseService.findActiveUsers();
        for (User user : activeUserList) {
            responseHandler.sendWeeklySchedule(user);
        }
    }

    public void sendAllUsersBets() {
        LogManager.log("Sending all users updated bet list");
        Set<String> activeIdList = databaseService.getActiveBetUserIds(weekNumber);
        for (String discordId : activeIdList) {
            User currentUser = databaseService.findUser(discordId);
            responseHandler.payoutMessage(currentUser, weekNumber);
        }
    }

    public void payWeeklyUsers() {
        LogManager.log("Paying all active users");
        List<User> activeUserList = databaseService.findActiveUsers();
        for (User user : activeUserList) {
            int newBalance = (int)Math.ceil(user.getToastyCoins() + weeklyPay);
            user.setToastyCoins(newBalance);
        }
        databaseService.updateAllUser(activeUserList);
    }
}
