package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {

    Integer id;

    @NotBlank(message = "description не должен быть пустым")
    String description;

    @NotNull(message = "requestor не должен быть null")
    Integer requestor;

    @FutureOrPresent(message = "created не должен быть в прошедшем времени")
    String created;

}
