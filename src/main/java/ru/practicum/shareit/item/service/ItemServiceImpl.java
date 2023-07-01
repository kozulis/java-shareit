package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
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
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto saveItem(Integer userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        Item item = ItemMapper.toItem(user, itemDto);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId()).orElseThrow(() ->
                    new NotFoundException(String.format("Запрос с id = %d не найден", item.getRequestId())));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> getAllByUserId(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page);

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
        List<BookingDto> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(List.of(item),
                        BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(toList());

        List<CommentDto> comments = commentRepository.findByItemOrderByIdAsc(item)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());

        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(
                    item,
                    getLastBooking(bookings),
                    getNextBooking(bookings),
                    comments
            );
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
    public List<ItemDto> searchItem(Integer userId, String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.search(text, page)
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
