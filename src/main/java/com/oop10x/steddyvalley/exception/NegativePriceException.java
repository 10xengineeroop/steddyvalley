package com.oop10x.steddyvalley.exception;

public class NegativePriceException extends RuntimeException {
    public NegativePriceException(String message) {
        super(message);
    }
}
