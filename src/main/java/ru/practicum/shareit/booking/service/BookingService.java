package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto saveBooking(int userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(int userId, Integer bookingId, Boolean approved);

    BookingResponseDto getById(int userId, Integer bookingId);

    List<BookingResponseDto> getByUser(int userId, String state);

    List<BookingResponseDto> getByItemsOwner(int userId, String state);

}

