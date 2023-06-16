package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

//    User save(User user);

//    List<User> findAll();

//    Optional<User> findById(int id);

//    User update(int id, User user);

//    void deleteById(int id);

}
