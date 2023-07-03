package ru.practicum.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.validation.OnCreate;
import ru.practicum.gateway.validation.OnUpdate;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Запрос на создание нового пользователя");
        return userClient.saveUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение списков пользователей");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long id) {
        log.info("Запрос на получение пользователя c id = {}", id);
        return userClient.getById(id);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long id, @Validated(OnUpdate.class)
    @RequestBody UserDto userDto) {
        log.info("Запрос на обновление данных пользователя c id = {}", id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long id) {
        log.info("Запрос на удаление пользователя");
        return userClient.delete(id);
    }

}
