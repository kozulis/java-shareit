package ru.practicum.server.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item, BookingDto last, BookingDto next, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toItem(User user, ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .requestId(itemDto.getRequestId())
                .build();
    }

    public static Set<ItemDto> toItemDtos(Set<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

}
