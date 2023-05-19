package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequest {

    int id;
    String description;
    Integer requestor;
    LocalDateTime created;

}
