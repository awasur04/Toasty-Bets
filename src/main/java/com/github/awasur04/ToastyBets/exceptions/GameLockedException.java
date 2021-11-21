package com.github.awasur04.ToastyBets.exceptions;

public class GameLockedException extends RuntimeException{
    public GameLockedException(String message) {
        super(message);
    }
}
