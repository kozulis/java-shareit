package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownBookingStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto saveBooking(Integer bookerId, BookingDto bookingDto) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", bookerId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", bookerId));
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

        if (item.getOwner().getId().equals(bookerId)) {
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
    @Transactional
    public BookingResponseDto approveBooking(Integer bookerId, Integer bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
                    log.warn("Бронирование с id = {} не найдено", bookingId);
                    return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
                }
        );

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Бронирование уже было подтверждено или отклонено");
            throw new ValidationException("Бронирование уже было подтверждено или отклонено");
        }

        if (!booking.getItem().getOwner().getId().equals(bookerId)) {
            log.warn("Изменить статус бронирования может только владелец вещи");
            throw new NotFoundException("Изменить статус бронирования может только владелец вещи");
        }

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getById(Integer bookerId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
                    log.warn("Бронирование с id = {} не найдено", bookingId);
                    return new NotFoundException(String.format("Бронирование с id = %d не найдено", bookingId));
                }
        );

        if (!booking.getItem().getOwner().getId().equals(bookerId) && !booking.getBooker().getId().equals(bookerId)) {
            log.warn("Получить данные о бронирования может только владелец вещи или тот, кто создал бронирование");
            throw new NotFoundException("Получить данные о бронирования может только владелец вещи или тот," +
                    " кто создал бронирование");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByUser(Integer userId, String state, Integer from, Integer size) {
        BookingState bookingState = getBookingState(state);
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
                }
        );
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> userBookings = bookingRepository.findByBooker(user, page);

        return getBookingByState(userBookings, bookingState)
                .stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByItemsOwner(Integer userId, String state, Integer from, Integer size) {
        var bookingState = getBookingState(state);
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
                }
        );
        PageRequest page = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> itemOwnerBookings = bookingRepository.findByItem_Owner(user, page);

        return getBookingByState(itemOwnerBookings, bookingState)
                .stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }


    private BookingState getBookingState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new UnknownBookingStateException(String.format("Unknown state: %s", state));
        }
    }

    private List<Booking> getBookingByState(List<Booking> bookingList, BookingState bookingState) {
        LocalDateTime now = LocalDateTime.now();
        Stream<Booking> bookingStream = bookingList.stream();
        switch (bookingState) {
            case CURRENT:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isBefore(now) &&
                        booking.getEnd().isAfter(now));
                break;
            case PAST:
                bookingStream = bookingStream.filter(booking -> booking.getEnd().isBefore(now));
                break;
            case FUTURE:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isAfter(now));
                break;
            case WAITING:
                bookingStream = bookingStream.filter(booking -> booking.getStatus().equals(BookingStatus.WAITING));
                break;
            case REJECTED:
                bookingStream = bookingStream.filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED));
                break;
        }
        return bookingStream.sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
    }

}
