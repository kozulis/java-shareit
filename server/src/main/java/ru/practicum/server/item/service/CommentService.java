package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;

public interface CommentService {

    CommentDto saveComment(Integer userId, Integer itemId, CommentDto commentDto);

}
