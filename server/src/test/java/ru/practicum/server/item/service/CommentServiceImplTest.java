package ru.practicum.server.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private final User author = User.builder().id(1).name("author").email("author@user.com").build();
    private final User owner = User.builder().id(2).name("owner").email("owner@user.com").build();
    private final Item item = Item.builder().id(3).name("itemName").description("itemDescription").available(true)
            .owner(owner).requestId(1).build();
    private final CommentDto commentDto = CommentDto.builder().text("comment").build();
    private final Comment comment = CommentMapper.toComment(commentDto, author, item);


    @Test
    @DisplayName("Добавление комментария к вещи после бронирования")
    void saveComment_returnComment() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto actualCommentDto = commentService.saveComment(author.getId(), item.getId(), commentDto);
        CommentDto expectCommentDto = CommentMapper.toCommentDto(comment);

        assertEquals(actualCommentDto, expectCommentDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1))
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                        any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Ошибка добавления комментария к вещи, если пользователь не найден")
    void saveComment_whenAuthorNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.saveComment(author.getId(), item.getId(), commentDto));

        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).findById(anyInt());
        verify(bookingRepository, never())
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                        any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Ошибка добавления комментария к вещи, если вещь не найдена")
    void saveComment_whenItemNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.saveComment(author.getId(), item.getId(), commentDto));

        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, never())
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                        any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Ошибка добавления комментария к вещи, если бронирование не найдено")
    void saveComment_whenBookingNotExist_thanValidationExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(ValidationException.class, () -> commentService.saveComment(author.getId(), item.getId(), commentDto));

        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1))
                .existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                        any(Item.class), any(User.class), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}