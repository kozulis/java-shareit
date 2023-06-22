package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private final User user = User.builder().id(1).name("user").email("user@user.com").build();
    private final UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();

    @Test
    @DisplayName("Добавление пользователя")
    void saveUser_returnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUserDto = userService.saveUser(userDto);
        UserDto expectUserDto = UserMapper.toUserDto(user);

        assertEquals(actualUserDto, expectUserDto);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void getAll_returnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUserDtoList = userService.getAll();
        List<UserDto> expectUserDtoList = List.of(UserMapper.toUserDto(user));

        assertEquals(actualUserDtoList, expectUserDtoList);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getById_returnUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getById(user.getId());
        UserDto expectUserDto = UserMapper.toUserDto(user);

        assertEquals(actualUserDto, expectUserDto);
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Ошибка получения пользователя по id, если пользователь не найден")
    void getById_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(user.getId()));
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    void updateUser_returnUser() {
        userDto.setName("userUpdate");
        userDto.setEmail("userUpdate@user.com");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));

        UserDto actualUserDto = userService.updateUser(user.getId(), userDto);

        assertEquals(actualUserDto, userDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Ошибка обновления данных пользователя, если пользователь не найден")
    void updateUser_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(user.getId()));
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("Удаление пользователя")
    void delete() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Ошибка удаления пользователя, если пользователь не найден")
    void delete_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(user.getId()));
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, never()).deleteById(anyInt());
    }
}