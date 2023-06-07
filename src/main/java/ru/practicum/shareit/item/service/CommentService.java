package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {

    CommentDto saveComment(Integer userId, Integer itemId, CommentDto commentDto);

}
