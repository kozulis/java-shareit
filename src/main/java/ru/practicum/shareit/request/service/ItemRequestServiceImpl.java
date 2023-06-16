package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto saveItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestor, itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Integer userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        setItemsByRequest(itemRequests);
        return ItemRequestMapper.toItemRequestDtoListWithItems(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(page).stream()
                .filter(itemRequest -> !itemRequest.getRequestor().equals(requestor))
                .collect(Collectors.toList());
        setItemsByRequest(itemRequests);
        return ItemRequestMapper.toItemRequestDtoListWithItems(itemRequests);
    }

    @Override
    public ItemRequestDto getById(Integer userId, Integer requestId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
                    log.warn("Запрос с id = {} не найден", requestId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );
        setItemsByRequest(List.of(itemRequest));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private void setItemsByRequest(List<ItemRequest> itemRequests) {
        List<Integer> itemRequestsIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Integer, Set<Item>> items = itemRepository.findAllByRequestIdIn(itemRequestsIds).stream()
                .collect(groupingBy(Item::getRequestId, toSet()));
        itemRequests.forEach(itemRequest ->
                itemRequest.setItems(items.getOrDefault(itemRequest.getId(), Collections.emptySet())));
    }

}
