package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {

    private int id;
    @NotBlank(message = "name не должен быть пустым", groups = OnCreate.class)
    private String name;
    @NotBlank(message = "description не должен быть пустым", groups = OnCreate.class)
    private String description;
    @NotNull(message = "available не должен быть null", groups = OnCreate.class)
    private Boolean available;

}
