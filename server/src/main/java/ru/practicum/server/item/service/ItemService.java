package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto saveItem(Integer userId, ItemDto itemDto);

    List<ItemDto> getAllByUserId(Integer userId, Integer from, Integer size);

    ItemDto getById(Integer userId, Integer id);

    ItemDto updateItem(Integer userId, Integer id, ItemDto itemDto);

    void deleteById(Integer id);

    List<ItemDto> searchItem(Integer userId, String text, Integer from, Integer size);

}
