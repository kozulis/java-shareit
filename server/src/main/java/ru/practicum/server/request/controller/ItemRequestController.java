package ru.practicum.server.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveNewRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
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
                                               Integer from,
                                               @RequestParam(defaultValue = "10")
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
