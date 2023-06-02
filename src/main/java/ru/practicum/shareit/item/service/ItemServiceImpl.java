package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemDto saveItem(int userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        Item item = ItemMapper.toItem(user, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAllByUserId(int userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(int userId, int id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", id));
                }
        );
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(int userId, int id, ItemDto itemDto) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", id));
                }
        );
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Обновить вещь может только ее владелец");
            throw new NotFoundException("Обновить вещь может только ее владелец");
        }
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void deleteById(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItem(int userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
