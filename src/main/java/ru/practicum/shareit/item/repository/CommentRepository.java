package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemOrderByIdAsc(Item item);

    List<Comment> findByItemIn(List<Item> items);

}
