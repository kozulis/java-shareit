package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class ItemDto {

    int id;
    @NotBlank(message = "name не должен быть пустым")
    String name;
    @NotBlank(message = "description не должен быть пустым")
    String description;
    @NotNull(message = "available не должен быть null")
    boolean available;
    @NotNull(message = "owner не должен быть null")
    int owner;
    ItemRequest request;

    public ItemDto(int id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
