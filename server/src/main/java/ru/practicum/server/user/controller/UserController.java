package ru.practicum.server.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        log.info("Запрос на создание нового пользователя");
        return userService.saveUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение списков пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") int id) {
        log.info("Запрос на получение пользователя c id = {}", id);
        return userService.getById(id);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") int id, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление данных пользователя c id = {}", id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") int id) {
        log.info("Запрос на удаление пользователя");
        userService.delete(id);
    }

}
