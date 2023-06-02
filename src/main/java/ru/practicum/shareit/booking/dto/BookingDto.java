package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Integer id;

    @NotEmpty(message = "дата бронирования не должна быть пустой", groups = OnCreate.class)
    @FutureOrPresent(message = "дата бронирования не должен быть в прошедшем времени")
    private LocalDateTime start;

    @NotEmpty(message = "дата окончания бронирования не должна быть пустой", groups = OnCreate.class)
    @FutureOrPresent(message = "дата окончания бронирования не должен быть в прошедшем времени")
    private LocalDateTime end;

    @NotEmpty(message = "item не должен быть пустой", groups = OnCreate.class)
    private Integer itemId;

    private Integer bookerId;

    private String status;

}
