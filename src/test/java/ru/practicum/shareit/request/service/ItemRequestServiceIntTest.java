package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntTest {

    private final UserService userService;
    private final ItemRequestService itemRequestService;

    private final UserDto requestorDto = UserDto.builder().name("requestor").email("requestor@user.com").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Нужна панталоны").build();

    @Test
    @DisplayName("Получение списка собственных запросов")
    void getOwnRequests() {
        UserDto savedRequestorDto = userService.saveUser(requestorDto);
        ItemRequestDto savedItemRequestDto = itemRequestService.saveItemRequest(savedRequestorDto.getId(), itemRequestDto);

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(savedRequestorDto.getId());

        assertEquals(ownRequests.size(), 1);
        assertEquals(ownRequests.get(0).getId(), savedItemRequestDto.getId());
        assertEquals(ownRequests.get(0).getDescription(), savedItemRequestDto.getDescription());
    }
}