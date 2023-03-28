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

        newUser.setId(newId());
        userStorage.add(newUser);
        log.info("A new user has been added: {}", newUser);

        return newUser;
    }

    public String deleteUser(int userId) {

        User user = userStorage.findById(userId);

        if (user == null) {
            throw new ObjectNotFoundException("The user with this id is not in the list.");
        } else {
            userStorage.delete(user);
        }

        return "user with id " + userId + " deleted";
    }

    public User put(User newUser) {

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
        userStorage.addToFriends(id, friendId);

        return "Users\n" + userStorage.findById(id) + "\nand\n" + userStorage.findById(friendId) +
                "\nare now friends with each other";
    }

    public String deleteFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Id is specified incorrectly");
        }

        searchId(id, friendId);
        userStorage.deleteFriends(id, friendId);

        return "Users\n" + userStorage.findById(id) + "\nand\n" + userStorage.findById(friendId) +
                "\nare no longer friends with each other";
    }

    public List<User> findFriendsById(int id) {

        List<User> result = new ArrayList<>();

        searchId(id);

        for (Integer friendId : userStorage.findFriendsById(id)) {
            if (friendId != null) result.add(userStorage.findById(friendId));
        }

        return result;
    }

    public List<User> mutualFriends(int id, int otherId) {

        List<User> result = new ArrayList<>();

        searchId(id, otherId);

        Collection<Integer> userOneFriends = userStorage.findFriendsById(id);
        Collection<Integer> userTwoFriends = userStorage.findFriendsById(otherId);

        if (userOneFriends.isEmpty() || userTwoFriends.isEmpty()) {
            return result;
        }

        for (Integer friendId : userOneFriends) {
            if (userTwoFriends.contains(friendId)) {
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

    public void deleteAllData() {
        userStorage.deleteAllData();
        id = 1;
    }
}
