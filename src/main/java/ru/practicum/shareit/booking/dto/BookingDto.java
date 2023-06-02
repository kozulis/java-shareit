package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Integer id;

    @NotEmpty
    @FutureOrPresent(message = "дата бронирования не должен быть в прошедшем времени")
    private LocalDateTime start;

    @NotEmpty
    @Future(message = "дата окончания бронирования не должен быть в прошедшем времени")
    private LocalDateTime end;

    @NotNull(message = "item не должен быть null")
    private Integer item;

    @NotNull(message = "booker не должен быть null")
    private Integer booker;

    private String status;

}
