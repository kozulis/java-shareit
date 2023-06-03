package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    //создан любым, подтвержден владельцем вещи
    @PostMapping
    public BookingResponseDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Validated(OnCreate.class)
                                             @RequestBody BookingDto bookingDto) {
        log.info("Добавление нового запроса на бронирование");
        return bookingService.saveBooking(userId, bookingDto);
    }

    // подтверждение или отклонение запроса
    // может быть выполнено только владельцем вещи
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение или отклонение " +
                "бронирования с id = {} владельцем с id = {}", bookingId, userId);
        return null;
    }

    // получение данных о конкретном бронировании
    // может быть выполнено либо автором бронирования, либо владельцем вещи
    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId) {
        log.info("Запрос на получение всех данных о бронировании с id = {}", bookingId);
        return null;
    }

    // список бронирований текущего пользователя.
    // должны возвращаться отсортированными по дате от более новых к более старым
    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка бронирований пользователя с id = {}", userId);
        return null;
    }

    // список бронирований для владельца вещей
    // должны возвращаться отсортированными по дате от более новых к более старым
    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(required = false,
                                                                    defaultValue = "ALL") String state) {
        log.info("Запрос на получение списка бронирований вещей владельцем с id = {}", userId);
        return null;
    }
}
