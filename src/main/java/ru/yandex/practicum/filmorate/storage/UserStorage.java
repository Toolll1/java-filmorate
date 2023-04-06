package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> findAll();

    User add(User user);

    User findById(int id);

    User update(User user);

    void delete(User user);

    void addToFriends(int id, int friendId);

    void deleteFriends(int id, int friendId);

    Collection<Integer> findFriendsById(int id);

    List<User> mutualFriends(int id, int otherId);
}
