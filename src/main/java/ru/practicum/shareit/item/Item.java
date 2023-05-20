package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    //    private User owner;
    private Integer owner;
    private Integer request;

    public Item(int id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
