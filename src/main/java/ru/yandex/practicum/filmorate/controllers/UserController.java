package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {

        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {

        for (User value : users.values()) {
            if (value.getEmail().equals(newUser.getEmail())) {
                throw new ValidateException("Пользователь с электронной почтой " +
                        newUser.getEmail() + " уже зарегистрирован.");
            }
        }

        validate(newUser);
        newUser.setId(newId());
        users.put(newUser.getId(), newUser);
        log.info("Новый пользователь: {}", newUser);

        return newUser;
    }

    @PutMapping
    public User put(@RequestBody User newUser) {

        validate(newUser);

        if (newUser.getId() == null) {
            newUser.setId(newId());
            users.put(newUser.getId(), newUser);
            log.info("Новый пользователь: {}", newUser);
        } else if (users.containsKey(newUser.getId())) {
            log.info("Пользователь: {} изменен на: {}", users.get(newUser.getId()), newUser);
            users.put(newUser.getId(), newUser);
        } else {
            throw new ValidateException("Пользователя с таким id нет в списке.");
        }

        return newUser;
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
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("дата рождения не может быть в будущем");
        }

        for (User value : users.values()) {
            if (value.getLogin().equals(newUser.getLogin())) {
                throw new ValidateException("Пользователь с таким логином " +
                        newUser.getLogin() + " уже зарегистрирован.");
            }
        }
    }
}
