package com.github.awasur04.ToastyBets.database;

import com.github.awasur04.ToastyBets.exceptions.UserNotFoundException;
import com.github.awasur04.ToastyBets.models.User;
import com.github.awasur04.ToastyBets.models.enums.PermissionLevel;
import com.github.awasur04.ToastyBets.repository.UserRepository;
import com.github.awasur04.ToastyBets.utilities.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DatabaseController {

    @Autowired
    private UserRepository userRepository;

    public User addNewUser(String discordId, String discordName) {
        try {
            User newUser = new User();
            newUser.setDiscordId(discordId);
            newUser.setDiscordName(discordName);
            newUser.setPermissionLevel(PermissionLevel.NORMAL);
            newUser.setTimeZone("UTC+0");
            newUser.setToastyCoins(1000);
            userRepository.save(newUser);
            return newUser;
        }catch (Exception e) {
            LogManager.error("Failed to create new user", e.getStackTrace().toString());
            return null;
        }
    }

    public User findUser(String discordId) {
        try {
            User currentUser = userRepository.findById(discordId).get();
            if (currentUser == null) {
                throw new UserNotFoundException("ID: " + discordId);
            }
            return currentUser;
        } catch (UserNotFoundException u) {
            return null;
        } catch (Exception e) {
            LogManager.error("Could not retrieve user from database: " + discordId, e.getStackTrace().toString());
            return null;
        }
    }

    public boolean updateUser(User user) {
        try {
            userRepository.save(user);
            return true;
        }catch (Exception e) {
            LogManager.error("Failed to update user", e.getStackTrace().toString());
            return false;
        }
    }
}
