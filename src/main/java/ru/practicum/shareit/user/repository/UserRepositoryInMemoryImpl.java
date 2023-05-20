package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryInMemoryImpl implements UserRepository {

    private int count = 1;

    private final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        checkEmail(user.getEmail());
        int id = getId(count);
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User update(int id, User user) {
        User oldUser = findById(id).orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", id);
                    return new NotFoundException(String.format("Пользователь с id %d не найден", id));
                }
        );
        if (!oldUser.getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
        }
        userMap.put(id, user);
        return user;
    }

    @Override
    public void deleteById(int id) {
        userMap.remove(id);
    }

    private int getId(int countId) {
        count++;
        return countId;
    }

    private void checkEmail(String email) {
        if (userMap.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            log.warn("Невозможно обновить данные. " +
                    "Пользователь с электронной почтой \"{}\" уже существует в хранилище", email);
            throw new AlreadyExistException(
                    String.format("Невозможно обновить данные. Данные \"%s\" уже существуют", email));
        }
    }
}
