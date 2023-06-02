package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto saveBooking(int userId, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingResponseDto updateBooking(int userId, Integer bookingId, Boolean approved) {
        return null;
    }

    @Override
    public BookingResponseDto getById(int userId, Integer bookingId) {
        return null;
    }

    @Override
    public List<BookingResponseDto> getByUser(int userId, String state) {
        return null;
    }

    @Override
    public List<BookingResponseDto> getByItemsOwner(int userId, String state) {
        return null;
    }

}
