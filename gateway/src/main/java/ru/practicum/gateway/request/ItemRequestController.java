package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.ItemRequestDto;
import ru.practicum.gateway.validation.OnCreate;


import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated(OnCreate.class)
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса вещи для бронирования");
        return itemRequestClient.saveItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка собственных запросов пользователя с id = {}", userId);
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0")
                                               @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                               Long from,
                                               @RequestParam(defaultValue = "10")
                                               @Positive(message = "Параметр 'size' должен быть больше 0")
                                                     Long size) {
        log.info("Получение списка запросов других пользователей");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получение запроса с id = {}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }

}
