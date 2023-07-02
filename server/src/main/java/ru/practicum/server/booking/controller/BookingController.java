package ru.practicum.server.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody BookingDto bookingDto) {
        log.info("Добавление нового запроса на бронирование");
        return bookingService.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение или отклонение " +
                "бронирования с id = {} владельцем с id = {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId) {
        log.info("Запрос на получение всех данных о бронировании с id = {}", bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0")
                                                      @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                                      Integer from,
                                                      @RequestParam(defaultValue = "10")
                                                      @Positive(message = "Параметр 'size' должен быть больше 0")
                                                      Integer size) {
        log.info("Запрос на получение списка бронирований пользователя с id = {}", userId);
        return bookingService.getByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(defaultValue = "ALL") String state,
                                                            @RequestParam(defaultValue = "0")
                                                            @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                                            Integer from,
                                                            @RequestParam(defaultValue = "10")
                                                            @Positive(message = "Параметр 'size' должен быть больше 0")
                                                            Integer size) {
        log.info("Запрос на получение списка бронирований вещей владельцем с id = {}", userId);
        return bookingService.getByItemsOwner(userId, state, from, size);
    }
}
