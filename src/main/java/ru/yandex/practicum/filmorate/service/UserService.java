package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> findAll() {

        return userStorage.findAll().values();
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

        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }

        newUser.setId(newId());
        userStorage.add(newUser);
        log.info("A new user has been added: {}", newUser);

        return newUser;
    }

    public User put(User newUser) {

        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }
        if (newUser.getId() == null) {
            newUser.setId(newId());
            userStorage.add(newUser);
            log.info("A new user has been added: {}", newUser);
        } else if (userStorage.findAll().containsKey(newUser.getId())) {
            log.info("User: {} changed to: {}", userStorage.findById(newUser.getId()), newUser);
            userStorage.update(newUser);
        } else {
            throw new ObjectNotFoundException("The user with this id is not in the list.");
        }

        return newUser;
    }

    public String addToFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Id is specified incorrectly");
        }

        searchId(id, friendId);

        User user1 = userStorage.findById(id);
        User user2 = userStorage.findById(friendId);

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        return "Users " + userStorage.findById(id) + " and " + userStorage.findById(friendId) +
                " are now friends with each other";
    }

    public String deleteFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Id is specified incorrectly");
        }

        searchId(id, friendId);

        User user1 = userStorage.findById(id);
        User user2 = userStorage.findById(friendId);

        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());

        return "Users " + userStorage.findById(id) + " and " + userStorage.findById(friendId) +
                " are no longer friends with each other";
    }

    public List<User> findFriendsById(int id) {

        List<User> result = new ArrayList<>();

        searchId(id);

        for (Integer friendId : userStorage.findById(id).getFriends()) {
            if (friendId != null) result.add(userStorage.findById(friendId));
        }

        return result;
    }

    public List<User> mutualFriends(int id, int otherId) {

        List<User> result = new ArrayList<>();

        searchId(id, otherId);

        if (userStorage.findById(id).getFriends().isEmpty() || userStorage.findById(otherId).getFriends().isEmpty()) {
            return result;
        }

        for (Integer friendId : userStorage.findById(id).getFriends()) {
            if (userStorage.findById(otherId).getFriends().contains(friendId)) {
                result.add(userStorage.findById(friendId));
            }
        }

        return result;
    }

    public void searchId(int... ids) {

        for (int id : ids) {
            if (!userStorage.findAll().containsKey(id)) {
                throw new ObjectNotFoundException("Id is specified incorrectly");
            }
        }
    }

    private int id = 1;

    protected int newId() {

        return id++;
    }
}
