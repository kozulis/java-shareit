package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") int userId,
                               @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        return itemService.saveItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllByUserId(userId);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int id) {
        return itemService.getById(userId, id);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable("itemId") int id, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") int id) {
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }

}
