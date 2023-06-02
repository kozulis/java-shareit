package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository/* extends JpaRepository<Item, Integer>*/ {

    Item save(Item item);

    List<Item> findAllByUserId(Integer userId);

    Optional<Item> findById(int id);

    Item update(int id, Item item);

    void deleteById(int id);

    List<Item> search(String text);

}
