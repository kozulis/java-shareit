package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto saveBooking(Integer userId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingResponseDto getById(Integer userId, Integer bookingId);

    List<BookingResponseDto> getByUser(Integer userId, String state);

    List<BookingResponseDto> getByItemsOwner(Integer userId, String state);

}

