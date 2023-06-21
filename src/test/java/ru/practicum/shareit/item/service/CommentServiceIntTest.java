package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentServiceIntTest {

    private final CommentService commentService;
    private final ItemService itemService;
    private final UserService userService;

    private final BookingService bookingService;

    LocalDateTime now = LocalDateTime.now();

    private final UserDto itemOwnerDto = UserDto.builder().name("itemOwner").email("itemOwner@user.com").build();
    private final UserDto bookerDto = UserDto.builder().name("booker").email("booker@user.com").build();

    private final ItemDto itemDto = ItemDto.builder().name("itemName").description("itemDescription").available(true).build();
    private final BookingDto bookingDto = BookingDto.builder().start(now.minusHours(2)).end(now.minusHours(1)).itemId(1).build();
    private final CommentDto commentDto = CommentDto.builder().text("commentText").build();

    @Test
    void saveComment() {
        UserDto savedItemOwnerDto = userService.saveUser(itemOwnerDto);
        UserDto savedBookerDto = userService.saveUser(bookerDto);
        ItemDto savedItemDto = itemService.saveItem(savedItemOwnerDto.getId(), itemDto);
        BookingResponseDto savedBookingResponseDto = bookingService.saveBooking(savedBookerDto.getId(), bookingDto);
        savedBookingResponseDto.setStatus(BookingStatus.APPROVED);

        CommentDto actualCommentDto = commentService.saveComment(savedBookerDto.getId(), savedItemDto.getId(), commentDto);

        assertNotNull(actualCommentDto.getId());
        assertEquals(actualCommentDto.getText(), commentDto.getText());
        assertEquals(actualCommentDto.getAuthorName(), bookerDto.getName());
    }
}