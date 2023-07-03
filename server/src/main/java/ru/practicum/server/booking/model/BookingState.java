package ru.practicum.server.booking.model;

public enum BookingState {
    ALL, /*все*/
    CURRENT, /*текущие*/
    PAST, /*завершенные*/
    FUTURE, /*будущие*/
    WAITING, /*ожидает подтверждения*/
    REJECTED, /*отклоненные*/
}
