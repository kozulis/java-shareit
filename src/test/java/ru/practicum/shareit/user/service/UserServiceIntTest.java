package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntTest {

    private final UserService userService;

    private final UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();

    @Test
    @DisplayName("Получение пользователя по id")
    void getById() {
        UserDto savesUserDto = userService.saveUser(userDto);

        UserDto actualUserDto = userService.getById(savesUserDto.getId());

        assertEquals(actualUserDto.getId(), 1);
        assertEquals(actualUserDto.getName(), userDto.getName());
        assertEquals(actualUserDto.getEmail(), userDto.getEmail());
    }
}