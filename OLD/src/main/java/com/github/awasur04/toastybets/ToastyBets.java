package com.github.awasur04.toastybets;

import com.github.awasur04.toastybets.game.GameManager;

import javax.security.auth.login.LoginException;


public class ToastyBets {


    public static void main(String[] args) throws LoginException {
        //START GAME MANAGER
        GameManager gm = new GameManager(args[0]);

        //SHUTDOWN HOOK TO CLOSE PROPERLY
        Runtime.getRuntime().addShutdownHook(new Thread(gm::closeProgram));
    }
}
