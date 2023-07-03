package ru.practicum.server.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntTest {

    private final ItemService itemService;
    private final UserService userService;

    private final UserDto itemOwnerDto = UserDto.builder().name("itemOwner").email("itemOwner@user.com").build();
    private final ItemDto itemDto = ItemDto.builder().name("itemName").description("itemDescription").available(true).build();
    private final ItemDto itemDto2 = ItemDto.builder().name("itemName2").description("itemDescription2").available(true).build();


    @Test
    @DisplayName("Добавление вещи")
    void saveItem() {
        UserDto savedItemOwnerDto = userService.saveUser(itemOwnerDto);

        ItemDto actualItemDto = itemService.saveItem(savedItemOwnerDto.getId(), itemDto);

        assertNotNull(actualItemDto.getId());
        assertEquals(actualItemDto.getName(), itemDto.getName());
        assertEquals(actualItemDto.getDescription(), itemDto.getDescription());
    }

    @Test
    @DisplayName("Получение списка вещей пользователя")
    void getAllByUserId() {
        UserDto savedItemOwnerDto = userService.saveUser(itemOwnerDto);
        itemService.saveItem(savedItemOwnerDto.getId(), itemDto);
        itemService.saveItem(savedItemOwnerDto.getId(), itemDto2);
        List<ItemDto> allByUserId = itemService.getAllByUserId(savedItemOwnerDto.getId(), 0, 10);

        assertEquals(allByUserId.size(), 2);

        assertEquals(allByUserId.get(0).getName(), itemDto.getName());
        assertEquals(allByUserId.get(0).getDescription(), itemDto.getDescription());
        assertEquals(allByUserId.get(1).getName(), itemDto2.getName());
        assertEquals(allByUserId.get(1).getDescription(), itemDto2.getDescription());
    }
}