package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание новой вещи");
        return itemClient.saveItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0")
                                             @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                             Long from,
                                             @RequestParam(defaultValue = "10")
                                             @Positive(message = "Параметр 'size' должен быть больше 0")
                                             Long size) {
        log.info("Запрос на получение списка вещей пользователя с id = {}", userId);
        return itemClient.getAllByUserId(userId, from, size);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long id) {
        log.info("Запрос на получение вещи с id = {}", id);
        return itemClient.getById(userId, id);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long id, @RequestBody ItemDto itemDto) {
        log.info("Запрос на изменение полей вещи");
        return itemClient.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable("itemId") Long id) {
        log.info("Запрос на удаление вещи");
        return itemClient.deleteById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam String text,
                                    @RequestParam(defaultValue = "0")
                                    @PositiveOrZero(message = "Параметр 'from' должен быть больше 0")
                                    Long from,
                                    @RequestParam(defaultValue = "10")
                                    @Positive(message = "Параметр 'size' должен быть больше 0")
                                    Long size) {
        log.info("Запрос на поиск вещи по названию или описанию, текст = \"{}\"", text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("itemId") Long id,
                                     @Validated(OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи c id = {} от пользователя с id = {}", id, userId);
        return itemClient.saveComment(userId, id, commentDto);
    }


}
