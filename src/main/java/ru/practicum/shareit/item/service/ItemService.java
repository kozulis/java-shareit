package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto saveItem(int userId, ItemDto itemDto);

    List<ItemDto> getAllByUserId(int userId);

    ItemDto getById(int userId, int id);

    ItemDto updateItem(int userId, int id, ItemDto itemDto);

    void deleteById(int id);

    List<ItemDto> searchItem(int userId, String text);

}
