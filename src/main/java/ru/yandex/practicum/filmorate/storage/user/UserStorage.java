package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> findAll();

    void add(User user);

    User findById(int id);

    void update(User user);

    void delete(User user);

    void addToFriends(int id, int friendId);

    void deleteFriends(int id, int friendId);

    void deleteAllData();

    Collection<Integer> findFriendsById(int id);
}
