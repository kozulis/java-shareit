package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto saveBooking(Integer bookerId, BookingDto bookingDto);

    BookingResponseDto approveBooking(Integer bookerId, Integer bookingId, Boolean approved);

    BookingResponseDto getById(Integer bookerId, Integer bookingId);

    List<BookingResponseDto> getByUser(Integer userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getByItemsOwner(Integer userId, String state, Integer from, Integer size);

}

