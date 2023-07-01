package ru.practicum.server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.server.validation.OnCreate;
import ru.practicum.server.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Integer id;

    @NotBlank(message = "name не должен быть пустым или null", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "email не должен быть пустым или null", groups = OnCreate.class)
    @Email(message = "email должен соответствовать форме адреса электронной почты",
            groups = {OnCreate.class, OnUpdate.class})
    private String email;

}
