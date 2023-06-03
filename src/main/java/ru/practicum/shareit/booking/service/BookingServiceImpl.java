package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
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
        User booker = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
                }
        );

        Integer itemId = bookingDto.getItemId();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", itemId);
                    return new NotFoundException(String.format("Вещь с id = %d не найдена", itemId));
                }
        );

        if (!item.getAvailable()) {
            log.warn("Вещь с id = {} недоступна для бронирования", itemId);
            throw new ValidationException(String.format("Вещь с id = %d недоступна для бронирования", itemId));
        }

        if (item.getOwner().getId().equals(userId)) {
            log.warn("Попытка забронировать собственную вещь");
            throw new NotFoundException("Попытка забронировать собственную вещь");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.warn("Время окончания раньше или равно времени начала бронирования");
            throw new ValidationException("Время окончания раньше или равно времени начала бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(int userId, Integer bookingId, Boolean approved) {
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
