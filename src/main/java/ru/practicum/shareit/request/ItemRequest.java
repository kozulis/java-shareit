package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    Integer id;
    String description;
    User requestor;
    LocalDateTime created = LocalDateTime.now();

}
