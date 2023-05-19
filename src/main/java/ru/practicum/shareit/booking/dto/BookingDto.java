package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private int id;
    @FutureOrPresent(message = "start не должен быть в прошедшем времени")
    private LocalDateTime start;
    @FutureOrPresent(message = "end не должен быть в прошедшем времени")
    private LocalDateTime end;
    @NotNull(message = "item не должен быть null")
    private Integer item;
    @NotNull(message = "booker не должен быть null")
    private Integer booker;
    @NotNull(message = "status не должен быть null")
    BookingStatus status;

}
