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

    private String name;

    private String email;

}
