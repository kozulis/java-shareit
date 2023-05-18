package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {

    //    @NotNull(message = "id не должен быть null")
    int id;

    @NotBlank(message = "email не должен быть пустым или null", groups = OnCreate.class)
    String name;

    @NotBlank(message = "email не должен быть пустым или null", groups = OnCreate.class)
    @Email(message = "email должен соответствовать форме адреса электронной почты",
            groups = {OnCreate.class, OnUpdate.class})
    String email;

}
