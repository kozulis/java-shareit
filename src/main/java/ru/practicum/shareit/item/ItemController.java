package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") int userId,
                               @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание новой вещи");
        return itemService.saveItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam(defaultValue = "0")
                                             @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                             Integer from,
                                             @RequestParam(defaultValue = "10")
                                             @Positive(message = "Параметр 'size' должен быть больше 0")
                                             Integer size) {
        log.info("Запрос на получение списка вещей пользователя с id = {}", userId);
        return itemService.getAllByUserId(userId, from, size);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int id) {
        log.info("Запрос на получение вещи с id = {}", id);
        return itemService.getById(userId, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable("itemId") int id, @RequestBody ItemDto itemDto) {
        log.info("Запрос на изменение полей вещи");
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") int id) {
        log.info("Запрос на удаление вещи");
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestParam String text,
                                    @RequestParam(defaultValue = "0")
                                    @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                    Integer from,
                                    @RequestParam(defaultValue = "10")
                                    @Positive(message = "Параметр 'size' должен быть больше 0")
                                    Integer size) {
        log.info("Запрос на поиск вещи по названию или описанию, текст = \"{}\"", text);
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveNewComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @PathVariable("itemId") int id,
                                     @Validated(OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи c id = {} от пользователя с id = {}", id, userId);
        return commentService.saveComment(userId, id, commentDto);
    }


}
