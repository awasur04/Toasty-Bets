package com.github.awasur04.toastybets.exceptions;

public class LowBalanceException extends RuntimeException{
    public LowBalanceException(String message) {
        super(message);
    }
}
