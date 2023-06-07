package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBooker(User user);

    List<Booking> findByItem_Owner(User owner);

    List<Booking> findByItem(Item item);

    boolean existsBookingByItemAndBookerAndStatusNotAndStartBefore(Item item, User booker,
                                                                   BookingStatus status, LocalDateTime time);
}
