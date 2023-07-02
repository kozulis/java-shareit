package ru.practicum.gateway.booking;

public enum BookingStatus {
    WAITING, /*новое бронирование, ожидает одобрения. значение по умолчанию*/
    APPROVED, /*бронирование подтверждено владельцем*/
    REJECTED, /*бронирование отклонено владельцем*/
    CANCELLED /*бронирование отменено создателем*/
}
