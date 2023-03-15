package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface UserStorage {

    @NotNull
    Map<Integer, User> findAll();

    void add(User user);

    User findById(int id);

    void update(User user);

    void delete(User user);
}
