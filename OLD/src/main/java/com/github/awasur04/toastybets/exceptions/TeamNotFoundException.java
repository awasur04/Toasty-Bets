package com.github.awasur04.toastybets.exceptions;

public class TeamNotFoundException extends RuntimeException{
    public TeamNotFoundException(String message) {
        super(message);
    }
}
