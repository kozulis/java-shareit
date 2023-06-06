package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<Booking> bookings) {
        ItemDto itemDto = toItemDto(item);
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(now) &&
                    (booking.getStatus().equals(BookingStatus.APPROVED) ||
                            booking.getStatus().equals(BookingStatus.WAITING))) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(booking));
            }
            if (booking.getStart().isBefore(now) &&
                    (booking.getStatus().equals(BookingStatus.APPROVED) ||
                            booking.getStatus().equals(BookingStatus.WAITING))) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
            }
        }
        return itemDto;
    }

    public static Item toItem(User user, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

}
