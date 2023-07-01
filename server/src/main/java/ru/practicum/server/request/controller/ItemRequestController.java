package ru.practicum.server.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveNewRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @Validated(OnCreate.class)
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса вещи для бронирования");
        return itemRequestService.saveItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получение списка собственных запросов пользователя с id = {}", userId);
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "0")
                                               @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                               Integer from,
                                               @RequestParam(defaultValue = "10")
                                               @Positive(message = "Параметр 'size' должен быть больше 0")
                                               Integer size) {
        log.info("Получение списка запросов других пользователей");
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable Integer requestId) {
        log.info("Получение запроса с id = {}", requestId);
        return itemRequestService.getById(userId, requestId);
    }

}
