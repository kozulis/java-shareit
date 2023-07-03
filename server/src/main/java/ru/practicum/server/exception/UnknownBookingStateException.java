package ru.practicum.server.exception;

public class UnknownBookingStateException extends RuntimeException {
    public UnknownBookingStateException(String message) {
        super(message);
    }
}
