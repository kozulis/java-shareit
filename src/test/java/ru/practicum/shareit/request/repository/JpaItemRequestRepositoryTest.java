package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class JpaItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private final User requestor = User.builder().name("requestor").email("requestor@user.com").build();
    private final ItemRequest itemRequest = ItemRequest.builder().description("Нужна отвертка").requestor(requestor)
            .created(LocalDateTime.now()).build();
    private final ItemRequest itemRequest1 = ItemRequest.builder().description("Нужна дрель").requestor(requestor)
            .created(LocalDateTime.now().plusMinutes(10)).build();

    @BeforeEach
    void save() {
        userRepository.save(requestor);
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest1);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение списка всех запросов, отсортированных по дате создания в убывающем порядке")
    void findAllByRequestorOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        assertEquals(2, itemRequests.size());
        assertEquals(itemRequests.get(0).getDescription(), "Нужна дрель");
        assertEquals(itemRequests.get(0).getRequestor(), requestor);
        assertEquals(itemRequests.get(0).getCreated(), itemRequest1.getCreated());

        assertEquals(itemRequests.get(1).getDescription(), "Нужна отвертка");
        assertEquals(itemRequests.get(1).getRequestor(), requestor);
        assertEquals(itemRequests.get(1).getCreated(), itemRequest.getCreated());

    }
}