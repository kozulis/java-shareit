package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") int userId,
                               @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.debug("Запрос на создание новой вещи");
        return itemService.saveItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("Запрос на получение списка вещей пользователя с id = {}", userId);
        return itemService.getAllByUserId(userId);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int id) {
        log.debug("Запрос на получение вещи с id = {}", id);
        return itemService.getById(userId, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable("itemId") int id, @RequestBody ItemDto itemDto) {
        log.debug("Запрос на изменение полей вещи");
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") int id) {
        log.debug("Запрос на удаление вещи");
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestParam String text) {
        log.debug("Запрос на поиск вещи по названию или описанию, текст = \"{}\"", text);
        return itemService.searchItem(userId, text);
    }

}
