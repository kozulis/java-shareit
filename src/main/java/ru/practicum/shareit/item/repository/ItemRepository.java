package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    List<Item> findAllDyUserId(Integer userId);

    Optional<Item> findById(int id);

    Item update(int userId, int id, Item item);

    void deleteById(int id);

    List<Item> search(String text);

}
