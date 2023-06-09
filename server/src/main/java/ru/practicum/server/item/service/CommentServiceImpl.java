package ru.practicum.server.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.CommentMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public CommentDto saveComment(Integer userId, Integer itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", userId));
                }
        );

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
                    log.warn("Вещь с id = {} не найдена", itemId);
                    return new NotFoundException(String.format("Вещь с id %d не найдена", itemId));
                }
        );

        Comment comment = CommentMapper.toComment(commentDto, author, item);

        if (!bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(item,
                author, BookingStatus.REJECTED, LocalDateTime.now())) {
            throw new ValidationException("Нельзя оставить комментарий к вещи, которая не была в аренде");
        }
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

}
