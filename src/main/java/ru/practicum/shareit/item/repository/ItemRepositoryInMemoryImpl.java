package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private int count = 1;

    private final Map<Integer, Item> itemMap = new HashMap<>();

    @Override
    public Item save(Item item) {
        int id = getId(count);
        item.setId(id);
        itemMap.put(id, item);
        return item;
    }

    @Override
    public List<Item> findAllByUserId(Integer userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(itemMap.get(id));
    }

    @Override
    public Item update(int userId, int id, Item item) {
        itemMap.put(id, item);
        return item;
    }

    @Override
    public void deleteById(int id) {
        try {
            itemMap.remove(id);
        } catch (NotFoundException e) {
            log.warn("Объект с id = {} не найден.", id);
        }
    }

    @Override
    public List<Item> search(String text) {
        return itemMap.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    private synchronized int getId(int countId) {
        count++;
        return countId;
    }

}
