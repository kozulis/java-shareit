package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {

    int id;
    @NotBlank(message = "description не должен быть пустым")
    String description;
    @NotNull(message = "requestor не должен быть null")
    User requestor;
    @FutureOrPresent(message = "created не должен быть в прошедшем времени")
    LocalDateTime created;

}
