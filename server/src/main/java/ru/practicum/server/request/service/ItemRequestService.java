package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnRequests(Integer userId);

    List<ItemRequestDto> getAllRequests(Integer userId, Integer from, Integer size);

    ItemRequestDto getById(Integer userId, Integer requestId);

}
