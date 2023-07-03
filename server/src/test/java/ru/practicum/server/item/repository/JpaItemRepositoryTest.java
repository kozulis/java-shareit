package ru.practicum.server.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final User owner = User.builder().name("user").email("user@user.com").build();
    private final Item item = Item.builder().name("Дрель").description("Простая дрель").available(true)
            .owner(owner).build();

    @BeforeEach
    void save() {
        userRepository.save(owner);
        itemRepository.save(item);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение списка вещей пользователя")
    void findAllByOwnerId() {
        Integer id = owner.getId();
        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderByIdAsc(id, Pageable.ofSize(1));
        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0).getName(), "Дрель");
        assertEquals(actualItems.get(0).getDescription(), "Простая дрель");
        assertEquals(actualItems.get(0).getAvailable(), true);
        assertEquals(actualItems.get(0).getOwner(), owner);
        assertNull(actualItems.get(0).getRequestId());
    }

    @Test
    @DisplayName("Поиск вещи по названию или описанию")
    void search() {
        List<Item> actualItems = itemRepository.search("ПроСт", Pageable.ofSize(1));
        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0).getName(), "Дрель");
        assertEquals(actualItems.get(0).getDescription(), "Простая дрель");
        assertEquals(actualItems.get(0).getAvailable(), true);
        assertEquals(actualItems.get(0).getOwner(), owner);
        assertNull(actualItems.get(0).getRequestId());
    }

    @Test
    @DisplayName("Получение списка вещей по списку запросов")
    void findAllByRequestIdIn() {
        User requestor = User.builder().name("requestor").email("requestor@user.com").build();
        userRepository.save(requestor);
        ItemRequest itemRequest = ItemRequest.builder().description("нужна отвертка").requestor(requestor)
                .created(LocalDateTime.now()).build();
        itemRequestRepository.save(itemRequest);
        Item itemForRequest = Item.builder().name("Отвертка").description("Аккумуляторная отвертка")
                .available(true).owner(owner).requestId(itemRequest.getId()).build();
        itemRepository.save(itemForRequest);

        List<Item> actualItems = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0).getName(), "Отвертка");
        assertEquals(actualItems.get(0).getDescription(), "Аккумуляторная отвертка");
        assertTrue(actualItems.get(0).getAvailable());
        assertEquals(actualItems.get(0).getOwner(), owner);
        assertEquals(actualItems.get(0).getRequestId(), itemRequest.getId());
    }
}