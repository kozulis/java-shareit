package ru.practicum.gateway.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.validation.OnCreate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Integer id;

    @NotBlank(message = "description не должен быть пустым", groups = OnCreate.class)
    private String description;

    private Integer requestorId;

    @FutureOrPresent(message = "created не должен быть в прошедшем времени")
    private LocalDateTime created;

    private Set<ItemDto> items;

}
