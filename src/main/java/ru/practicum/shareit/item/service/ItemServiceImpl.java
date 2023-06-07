package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
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
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto saveItem(Integer userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        Item item = ItemMapper.toItem(user, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAllByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        List<Item> userItems = itemRepository.findByOwner(user);
        return userItems
                .stream()
                .map(item -> ItemMapper.toItemDto(item, commentRepository.findByItemOrderByIdAsc(item),
                        bookingRepository.findByItem(item)))
                .sorted(this::compareDates)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Integer userId, Integer id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", id);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", id));
                }
        );
        List<Booking> bookings = bookingRepository.findByItem(item);
        List<Comment> comments = commentRepository.findByItemOrderByIdAsc(item);
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item, comments, bookings);
        }
        return ItemMapper.toItemDto(item, comments);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Integer userId, Integer id, ItemDto itemDto) {
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
    public void deleteById(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItem(Integer userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private int compareDates(ItemDto itemDto1, ItemDto itemDto2) {
        if (itemDto1.getNextBooking() == null && itemDto2.getNextBooking() == null) return 0;
        if (itemDto1.getNextBooking() == null) return 1;
        if (itemDto2.getNextBooking() == null) return -1;
        return -itemDto1.getNextBooking().getStart().compareTo(itemDto2.getNextBooking().getStart());
    }

}
