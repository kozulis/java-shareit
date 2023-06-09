package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {

    Integer id;

    @NotBlank(message = "description не должен быть пустым")
    String description;

    @NotNull(message = "requestor не должен быть null")
    Integer requestor;

    @FutureOrPresent(message = "created не должен быть в прошедшем времени")
    String created;

}
