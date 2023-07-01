package ru.practicum.gateway.exception;

public class UnknownBookingStateException extends RuntimeException {
    public UnknownBookingStateException(String message) {
        super(message);
    }
}
