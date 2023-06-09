package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
        userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        List<Item> userItems = itemRepository.findAllByOwnerId(userId);

        Map<Integer, List<CommentDto>> comments = commentRepository.findByItemIn(userItems)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(groupingBy(CommentDto::getItemId, toList()));

        Map<Integer, List<BookingDto>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(userItems,
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(groupingBy(BookingDto::getItemId, toList()));

        return userItems
                .stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        getLastBooking(bookings.get(item.getId())),
                        getNextBooking(bookings.get(item.getId())),
                        comments.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(toList());
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
                .collect(toList());
    }

    private BookingDto getLastBooking(List<BookingDto> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings
                .stream()
                .filter(bookingDto -> bookingDto.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingDto::getStart))
                .orElse(null);
    }

    private BookingDto getNextBooking(List<BookingDto> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings
                .stream()
                .filter(bookingDto -> bookingDto.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

}
