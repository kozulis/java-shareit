package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class BookingDto {

    int id;
    @FutureOrPresent(message = "start не должен быть в прошедшем времени")
    LocalDateTime start;
    @FutureOrPresent(message = "end не должен быть в прошедшем времени")
    LocalDateTime end;
    @NotNull(message = "item не должен быть null")
    Item item;
    @NotNull(message = "booker не должен быть null")
    User booker;
    @NotNull(message = "status не должен быть null")
    BookingStatus status;

}
