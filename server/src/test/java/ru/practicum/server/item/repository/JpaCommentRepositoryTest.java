package ru.practicum.server.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User author = User.builder().name("author").email("author@user.com").build();
    private final User owner = User.builder().name("user").email("user@user.com").build();
    private final Item item = Item.builder().name("Дрель").description("Простая дрель").available(true)
            .owner(owner).build();
    private final Comment commentPositive = Comment.builder().text("Крутая дрель. Сделал 100 дыр").item(item)
            .author(author).created(LocalDateTime.now()).build();
    private final Comment commentNegative = Comment.builder().text("Плохая дрель. Отвалилась кнопка").item(item)
            .author(author).created(LocalDateTime.now().plusHours(1)).build();


    @BeforeEach
    void save() {
        userRepository.save(author);
        userRepository.save(owner);
        itemRepository.save(item);
        commentRepository.save(commentPositive);
        commentRepository.save(commentNegative);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение списка комментариев к вещи, отсортированных по id в возрастающем порядке")
    void findByItemOrderByIdAsc() {
        List<Comment> actualComments = commentRepository.findByItemOrderByIdAsc(item);
        assertEquals(2, actualComments.size());
        assertEquals(actualComments.get(0).getText(), "Крутая дрель. Сделал 100 дыр");
        assertEquals(actualComments.get(0).getItem(), item);
        assertEquals(actualComments.get(0).getItem().getOwner(), owner);
        assertEquals(actualComments.get(0).getAuthor(), author);
        assertNotNull(actualComments.get(0).getCreated());

        assertEquals(actualComments.get(1).getText(), "Плохая дрель. Отвалилась кнопка");
        assertEquals(actualComments.get(1).getItem(), item);
        assertEquals(actualComments.get(1).getItem().getOwner(), owner);
        assertEquals(actualComments.get(1).getAuthor(), author);
        assertNotNull(actualComments.get(1).getCreated());
    }

    @Test
    @DisplayName("Получение списка комментариев для списка вещей")
    void findByItemIn() {
        Item item1 = Item.builder().name("Отвертка").description("Аккумуляторная отвертка").available(true)
                .owner(owner).build();
        itemRepository.save(item1);
        Comment commentPositive1 = Comment.builder().text("Дрель улёт. Улетела в окно").item(item1)
                .author(author).created(LocalDateTime.now().plusHours(2)).build();
        commentRepository.save(commentPositive1);

        List<Comment> actualComments = commentRepository.findByItemIn(List.of(item, item1));

        assertEquals(3, actualComments.size());
        assertEquals(actualComments.get(0).getText(), "Крутая дрель. Сделал 100 дыр");
        assertEquals(actualComments.get(0).getItem(), item);
        assertEquals(actualComments.get(0).getItem().getOwner(), owner);
        assertEquals(actualComments.get(0).getAuthor(), author);
        assertEquals(actualComments.get(0).getCreated(), commentPositive.getCreated());

        assertEquals(actualComments.get(1).getText(), "Плохая дрель. Отвалилась кнопка");
        assertEquals(actualComments.get(1).getItem(), item);
        assertEquals(actualComments.get(1).getItem().getOwner(), owner);
        assertEquals(actualComments.get(1).getAuthor(), author);
        assertEquals(actualComments.get(1).getCreated(), commentNegative.getCreated());

        assertEquals(actualComments.get(2).getText(), "Дрель улёт. Улетела в окно");
        assertEquals(actualComments.get(2).getItem(), item1);
        assertEquals(actualComments.get(2).getItem().getOwner(), owner);
        assertEquals(actualComments.get(2).getAuthor(), author);
        assertEquals(actualComments.get(2).getCreated(), commentPositive1.getCreated());
    }
}