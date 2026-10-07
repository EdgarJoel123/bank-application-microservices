package com.devsu.hackerearth.backend.account.exception;

public class InsufficientBalanceException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException() {
        super("Saldo no disponible");
    }
}
