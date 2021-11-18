package com.github.awasur04.ToastyBets.database;

import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.PermissionLevel;
import com.github.awasur04.ToastyBets.repository.UserRepository;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class DatabaseService {
    @Autowired
    private UserRepository userRepository;

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
}
