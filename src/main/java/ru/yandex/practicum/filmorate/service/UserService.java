package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> findAll() {

        return new ArrayList<>(userStorage.findAll().values());
    }

    public User findById(int userId) {

        User user = userStorage.findById(userId);

        if (user == null) {
            throw new ObjectNotFoundException("The user with this id is not in the list.");
        }

        return user;
    }

    public User create(User newUser) {

        for (User user : userStorage.findAll().values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new ValidateException("The user with email " + newUser.getEmail() + " is already registered.");
            }
        }

        User user = userStorage.add(newUser);

        log.info("A new user has been added: {}", user);

        return user;
    }

    public void deleteUser(int userId) {

        User user = userStorage.findById(userId);

        if (user == null) {
            throw new ObjectNotFoundException("The user with this id is not in the list.");
        } else {
            userStorage.delete(user);
        }

        log.info("user with id {} deleted", userId);
    }

    public User put(User newUser) {

        if (newUser.getId() == null) {
            User user = userStorage.add(newUser);
            log.info("A new user has been added: {}", user);
        } else if (userStorage.findAll().containsKey(newUser.getId())) {
            User user = userStorage.update(newUser);
            log.info("User: {} changed to: {}", userStorage.findById(newUser.getId()), user);
        } else {
            throw new ObjectNotFoundException("The user with this id is not in the list.");
        }

        return newUser;
    }

    public void addToFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Id is specified incorrectly");
        }

        isExist(id, friendId);
        userStorage.addToFriends(id, friendId);

        log.info("User {} and {} are now friends with each other", userStorage.findById(id),
                userStorage.findById(friendId));
    }

    public void deleteFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Id is specified incorrectly");
        }

        isExist(id, friendId);
        userStorage.deleteFriends(id, friendId);

        log.info("User {} and {} re no longer friends with each other", userStorage.findById(id),
                userStorage.findById(friendId));
    }

    public List<User> findFriendsById(int id) {

        List<User> result = new ArrayList<>();

        isExist(id);

        for (Integer friendId : userStorage.findFriendsById(id)) {
            if (friendId != null) result.add(userStorage.findById(friendId));
        }

        return result;
    }

    public List<User> mutualFriends(int id, int otherId) {

        isExist(id, otherId);

        return userStorage.mutualFriends(id, otherId);
    }

    public void isExist(int... ids) {

        for (int id : ids) {
            if (!userStorage.findAll().containsKey(id)) {
                throw new ObjectNotFoundException("Id is specified incorrectly");
            }
        }
    }
}
