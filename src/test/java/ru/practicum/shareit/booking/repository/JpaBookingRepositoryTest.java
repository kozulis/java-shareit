package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class JpaBookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User owner = User.builder().name("user").email("user@user.com").build();
    private final User booker = User.builder().name("booker").email("booker@user.com").build();
    private final Item item = Item.builder().name("Дрель").description("Простая дрель").available(true)
            .owner(owner).build();
    private final Booking booking = Booking.builder().start(LocalDateTime.now()).end(LocalDateTime.now()
            .plusHours(1)).item(item).booker(booker).status(BookingStatus.WAITING).build();

    @BeforeEach
    void save() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение списка бронирований пользователя")
    void findByBooker() {
        List<Booking> actualBookings = bookingRepository.findByBooker(booker, Pageable.ofSize(1));
        assertEquals(1, actualBookings.size());
        assertEquals(actualBookings.get(0).getItem(), item);
        assertEquals(actualBookings.get(0).getBooker(), booker);
        assertNotNull(actualBookings.get(0).getStart());
        assertNotNull(actualBookings.get(0).getEnd());
        assertEquals(actualBookings.get(0).getStatus(), BookingStatus.WAITING);
    }

    @Test
    @DisplayName("Получение списка бронирований владельца вещи")
    void findByItem_Owner() {
        List<Booking> actualBookings = bookingRepository.findByItem_Owner(owner, Pageable.ofSize(1));
        assertEquals(1, actualBookings.size());
        assertEquals(actualBookings.get(0).getItem(), item);
        assertEquals(actualBookings.get(0).getBooker(), booker);
        assertNotNull(actualBookings.get(0).getStart());
        assertNotNull(actualBookings.get(0).getEnd());
        assertEquals(actualBookings.get(0).getStatus(), BookingStatus.WAITING);
    }

    @Test
    @DisplayName("Получение бронирований списка вещей")
    void findAllByItemInAndStatusOrderByStartAsc() {
        Booking booking1 = Booking.builder().start(LocalDateTime.now().plusMinutes(10)).end(LocalDateTime.now()
                .plusHours(1)).item(item).booker(booker).status(BookingStatus.WAITING).build();
        bookingRepository.save(booking1);

        List<Booking> actualBookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(List.of(item),
                BookingStatus.WAITING);

        assertEquals(2, actualBookings.size());
        assertEquals(actualBookings.get(0), booking);
        assertEquals(actualBookings.get(0).getItem(), item);
        assertEquals(actualBookings.get(0).getBooker(), booker);
        assertNotNull(actualBookings.get(0).getStart());
        assertNotNull(actualBookings.get(0).getEnd());
        assertEquals(actualBookings.get(0).getStatus(), BookingStatus.WAITING);

        assertEquals(actualBookings.get(1), booking1);
        assertEquals(actualBookings.get(1).getItem(), item);
        assertEquals(actualBookings.get(1).getBooker(), booker);
        assertNotNull(actualBookings.get(1).getStart());
        assertNotNull(actualBookings.get(1).getEnd());
        assertEquals(actualBookings.get(1).getStatus(), BookingStatus.WAITING);
    }

    @Test
    @DisplayName("Проверка наличия бронирования по вещи и владельцу бронирования")
    void existsBookingByItemAndBookerAndStatusNotAndStartBefore() {
        Boolean booking = bookingRepository.existsBookingByItemAndBookerAndStatusNotAndStartBefore(
                item, booker, BookingStatus.APPROVED, LocalDateTime.now().plusMinutes(5));
        assertEquals(booking, true);
    }
}