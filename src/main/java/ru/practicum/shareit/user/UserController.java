package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveNewUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.debug("Запрос на создание нового пользователя");
        return userService.saveUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Запрос на получение списков пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") int id) {
        log.debug("Запрос на получение пользователя c id = {}", id);
        return userService.getById(id);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") int id, @Validated(OnUpdate.class)
    @RequestBody UserDto userDto) {
        log.debug("Запрос на обновление данных пользователя c id = {}", id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") int id) {
        log.debug("Запрос на удаление пользователя");
        userService.delete(id);
    }

}
