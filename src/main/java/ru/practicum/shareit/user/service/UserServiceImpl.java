package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", id);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", id));
                }
        );
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(int id, UserDto userDto) {
        UserDto updatedUserDto = getById(id);

        Optional.ofNullable(userDto.getName()).ifPresent(updatedUserDto::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(updatedUserDto::setEmail);

        User user = UserMapper.toUser(updatedUserDto);

        return UserMapper.toUserDto(userRepository.update(id, user));
    }

    public void delete(int id) {
        getById(id);
        userRepository.deleteById(id);
    }

}
