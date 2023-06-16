package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.OnCreate;

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

    // Запросы сортируются по дате создания: от более новых к более старым
    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получение списка собственных запросов пользователя с id = {}", userId);
        return itemRequestService.getOwnRequests(userId);
    }

    // Запросы сортируются по дате создания: от более новых к более старым
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение списка запросов других пользователей");
        if (from < 0) {
            throw new ValidationException("Параметр \"from\" не должен быть меньше 0");
        }
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable Integer requestId) {
        log.info("Получение запроса с id = {}", requestId);
        return itemRequestService.getById(userId, requestId);
    }

}
