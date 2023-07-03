package ru.practicum.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.validation.OnCreate;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Integer id;

    @NotBlank(message = "name не должен быть пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "description не должен быть пустым", groups = OnCreate.class)
    private String description;

    @NotNull(message = "available не должен быть null", groups = OnCreate.class)
    private Boolean available;

    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;

}
