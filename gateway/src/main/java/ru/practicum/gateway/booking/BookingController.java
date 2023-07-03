package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated(OnCreate.class)
                                                 @RequestBody BookingDto bookingDto) {
        log.info("Добавление нового запроса на бронирование");
        return bookingClient.saveBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение или отклонение " +
                "бронирования с id = {} владельцем с id = {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Запрос на получение всех данных о бронировании с id = {}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                    @RequestParam(defaultValue = "0")
                                                    @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                                    Long from,
                                                    @RequestParam(defaultValue = "10")
                                                    @Positive(message = "Параметр 'size' должен быть больше 0")
                                                    Long size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Запрос на получение списка бронирований пользователя с id = {}", userId);
        return bookingClient.getByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                          @RequestParam(defaultValue = "0")
                                                          @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                                          Long from,
                                                          @RequestParam(defaultValue = "10")
                                                          @Positive(message = "Параметр 'size' должен быть больше 0")
                                                          Long size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Запрос на получение списка бронирований вещей владельцем с id = {}", userId);
        return bookingClient.getByItemsOwner(userId, state, from, size);
    }
}
