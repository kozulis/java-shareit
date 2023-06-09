package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Integer id;

    @NotNull(message = "дата бронирования не должна быть пустой", groups = OnCreate.class)
    @FutureOrPresent(message = "дата начала бронирования не может быть в прошедшем времени", groups = OnCreate.class)
    private LocalDateTime start;

    @NotNull(message = "дата окончания бронирования не должна быть пустой", groups = OnCreate.class)
    @FutureOrPresent(message = "дата окончания бронирования не может быть в прошедшем времени", groups = OnCreate.class)
    private LocalDateTime end;

    @NotNull(message = "item не должен быть пустой", groups = OnCreate.class)
    private Integer itemId;

    private Integer bookerId;

    private String status;

}
