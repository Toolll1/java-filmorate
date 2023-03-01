package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new LinkedHashMap<>();

    @Override
    public Map<Integer, User> findAll() {

        return users;
    }

    @Override
    public void add(User user) {

        users.put(user.getId(), user);
    }

    @Override
    public User findById(int id) {

        return users.get(id);
    }

    @Override
    public void update(User user) {

        users.put(user.getId(), user);
    }

    @Override
    public void delete(User user) {

        users.remove(user.getId());
    }
}
