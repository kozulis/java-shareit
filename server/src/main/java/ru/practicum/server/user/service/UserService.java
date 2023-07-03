package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(int id);

    UserDto updateUser(int id, UserDto userDto);

    void delete(int id);

}
