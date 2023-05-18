package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private int count = 1;

    private final Map<Integer, User> userMap = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User save(User user) {
        checkEmail(user.getEmail());
        int id = getId(count);
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User update(int id, User user) {
        User newUser = findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", id)));
        if (!newUser.getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
        }
        userMap.put(id, user);
        return user;
    }

    @Override
    public void deleteById(int id) {
        userMap.remove(id);
    }

    private synchronized int getId(int countId) {
        count++;
        return countId;
    }

    private void checkEmail(String email) {
        if (userMap.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new AlreadyExistException(String.format("Данные %s уже существуют", email));
        }
    }
}
