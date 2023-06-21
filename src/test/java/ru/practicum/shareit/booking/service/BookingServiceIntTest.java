package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    LocalDateTime now = LocalDateTime.now();
    private final UserDto ownerDto = UserDto.builder().name("owner").email("owner@user.com").build();
    private final UserDto bookerDto = UserDto.builder().name("booker").email("booker@user.com").build();
    private final ItemDto itemDto = ItemDto.builder().name("itemName")
            .description("itemDescription").available(true).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .start(now.plusHours(1)).end(now.plusHours(2)).itemId(1).build();


    @Test
    void getByItemsOwner() {
        UserDto savedOwnerDto = userService.saveUser(ownerDto);
        UserDto savedBookerDto = userService.saveUser(bookerDto);
        ItemDto savedItemDto = itemService.saveItem(savedOwnerDto.getId(), itemDto);
        BookingResponseDto savedBookingResponseDto = bookingService.saveBooking(savedBookerDto.getId(), bookingDto);

        List<BookingResponseDto> bookings = bookingService.getByItemsOwner(savedOwnerDto.getId(), "ALL", 0, 10);
        assertEquals(savedOwnerDto.getId(), 1);
        assertEquals(savedBookingResponseDto.getId(), 1);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(bookings.get(0).getItem().getId(), savedItemDto.getId());
        assertEquals(bookings.get(0).getBooker().getId(), savedBookerDto.getId());
    }
}