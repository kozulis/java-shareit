package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto getById(int id);

    List<UserDto> getAll();

    void delete(int id);

    UserDto updateUser(int id, UserDto userDto);

}
