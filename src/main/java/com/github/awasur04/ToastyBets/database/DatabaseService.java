package com.github.awasur04.ToastyBets.database;

import com.github.awasur04.ToastyBets.models.Bet;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.PermissionLevel;
import com.github.awasur04.ToastyBets.repository.BetRepository;
import com.github.awasur04.ToastyBets.repository.UserRepository;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class DatabaseService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @Value("#{new Integer('${user.default.toastycoin.starting}')}")
    private int startingBalance;
    @Value("#{new Integer('${user.default.toastycoin.pay}')}")
    private int weeklyPay;
    @Value("${user.default.timezone}")
    private String startingTimezone;

    public User addNewUser(String discordId, String discordName) {
        try {
            User newUser = new User();
            newUser.setDiscordId(discordId);
            newUser.setDiscordName(discordName);
            newUser.setPermissionLevel(PermissionLevel.NORMAL);
            newUser.setTimeZone(startingTimezone);
            newUser.setToastyCoins(startingBalance);
            userRepository.save(newUser);
            LogManager.log("Successfully created new user: " + newUser.getDiscordName());
            return newUser;
        }catch (Exception e) {
            LogManager.error("Failed to create new user", e.getMessage());
            return null;
        }
    }

    public User findUser(String discordId) {
        try {
            User currentUser = userRepository.findById(discordId).get();
            return currentUser;
        } catch (NoSuchElementException ne) {
            return null;
        } catch (Exception e) {
            LogManager.error("Could not retrieve user from database: " + discordId, e.getMessage());
            return null;
        }
    }

    public boolean updateUser(User user) {
        try {
            userRepository.save(user);
            return true;
        }catch (Exception e) {
            LogManager.error("Failed to update user", e.getMessage());
            return false;
        }
    }

    public boolean saveNewBet(Bet bet) {
        try {
            betRepository.save(bet);
            return true;
        } catch(Exception e) {
            LogManager.error("Failed to create new bet database entry ", e.getMessage());
            return false;
        }
    }

    public Bet findBet(int betId) {
        try {
            Bet currentBet = betRepository.findById(betId).get();
            return currentBet;
        } catch (NoSuchElementException ne) {
            return null;
        } catch (Exception e) {
            LogManager.error("Unable to retrieve bet from database: ", e.getMessage());
            return null;
        }
    }

    public boolean updateBet(Bet bet) {
        try {
            betRepository.save(bet);
            return true;
        }catch (Exception e) {
            LogManager.error("Failed to update bet: " + bet.getBetId(), e.getMessage());
            return false;
        }
    }

    public List<Bet> findActiveBets() {
        try {
            return betRepository.findActiveBets();
        }catch(Exception e) {
            LogManager.error("Failed to find active bets ", e.getMessage());
            return null;
        }
    }
}
