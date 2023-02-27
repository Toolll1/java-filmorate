package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public Map<Integer, User> findAll() {

        return userStorage.findAll();
    }

    public User findById(int userId) {

        if (userStorage.findAll().containsKey(userId)) {
            return userStorage.findAll().get(userId);
        } else {
            throw new ObjectNotFoundException("Пользователя с таким id нет в списке.");
        }
    }

    public User create(User newUser) {

        for (User value : userStorage.findAll().values()) {
            if (value.getEmail().equals(newUser.getEmail())) {
                throw new ValidateException("Пользователь с электронной почтой " +
                        newUser.getEmail() + " уже зарегистрирован.");
            }
        }

        validate(newUser);
        newUser.setId(newId());
        userStorage.addUser(newUser);
        log.info("Добавлен новый пользователь: {}", newUser);

        return newUser;
    }

    public User put(User newUser) {

        validate(newUser);

        if (newUser.getId() == null) {
            newUser.setId(newId());
            userStorage.addUser(newUser);
            log.info("Добавлен новый пользователь: {}", newUser);
        } else if (userStorage.findAll().containsKey(newUser.getId())) {
            log.info("Пользователь: {} изменен на: {}", userStorage.findAll().get(newUser.getId()), newUser);
            userStorage.addUser(newUser);
        } else {
            throw new ObjectNotFoundException("Пользователя с таким id нет в списке.");
        }

        return newUser;
    }

    public String addToFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Некорректно указан id");
        }

        searchId(id, friendId);

        Set<Integer> friends;
        if (userStorage.findAll().get(id).getFriends() != null) {
            friends = userStorage.findAll().get(id).getFriends();
        } else {
            friends = new HashSet<>();
        }
        friends.add(friendId);
        userStorage.findAll().get(id).setFriends(friends);

        Set<Integer> friends1;
        if (userStorage.findAll().get(friendId).getFriends() != null) {
            friends1 = userStorage.findAll().get(friendId).getFriends();
        } else {
            friends1 = new HashSet<>();
        }
        friends1.add(id);
        userStorage.findAll().get(friendId).setFriends(friends1);

        return "Пользователи" + userStorage.findAll().get(id) + " и " + userStorage.findAll().get(friendId) +
                " теперь в друзьях друг у друга";
    }

    public String deleteFriends(int id, int friendId) {

        if (id == friendId) {
            throw new ObjectNotFoundException("Некорректно указан id");
        }

        searchId(id, friendId);

        Set<Integer> friends;
        if (userStorage.findAll().get(id).getFriends() != null
                && userStorage.findAll().get(id).getFriends().contains(friendId)) {
            friends = userStorage.findAll().get(id).getFriends();
        } else {
            throw new ObjectNotFoundException("Некорректно указан id");
        }
        friends.remove(friendId);
        userStorage.findAll().get(id).setFriends(friends);

        Set<Integer> friends1;
        if (userStorage.findAll().get(friendId).getFriends() != null
                && userStorage.findAll().get(friendId).getFriends().contains(id)) {
            friends1 = userStorage.findAll().get(friendId).getFriends();
        } else {
            throw new ObjectNotFoundException("Некорректно указан id");
        }
        friends1.remove(id);
        userStorage.findAll().get(friendId).setFriends(friends1);

        return "Пользователи" + userStorage.findAll().get(id) + " и " + userStorage.findAll().get(friendId) +
                " теперь не в друзьях друг у друга";
    }

    public List<User> findFriendsById(int id) {

        List<User> result = new ArrayList<>();

        searchId(id);

        for (Integer friendId : userStorage.findAll().get(id).getFriends()) {
            if (friendId != null) result.add(userStorage.findAll().get(friendId));
        }

        return result;
    }

    public List<User> mutualFriends(int id, int otherId) {

        List<User> result = new ArrayList<>();

        searchId(id, otherId);

        if (userStorage.findAll().get(id).getFriends() == null
                || userStorage.findAll().get(id).getFriends().isEmpty()
                || userStorage.findAll().get(otherId).getFriends() == null
                || userStorage.findAll().get(otherId).getFriends().isEmpty()) {
            return result;
        }

        for (Integer friendId : userStorage.findAll().get(id).getFriends()) {
            if (userStorage.findAll().get(otherId).getFriends().contains(friendId)) {
                result.add(userStorage.findAll().get(friendId));
            }
        }

        return result;
    }

    public void searchId(int... ids) {

        for (int id : ids) {
            if (!userStorage.findAll().containsKey(id)) {
                throw new ObjectNotFoundException("Некорректно указан id");
            }
        }
    }

    private int id = 1;

    protected int newId() {

        return id++;
    }

    private void validate(User newUser) {

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            throw new ValidateException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidateException("логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("дата рождения не может быть в будущем");
        } else if (newUser.getBirthday() == null) {
            throw new ValidateException("дата рождения не заполнена");
        }
    }
}
