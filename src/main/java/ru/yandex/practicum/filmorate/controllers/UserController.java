package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidCreateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, User> users = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {

        log.info("Количество пользователей в текущий момент: {}", users.size());

        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {

        checkingTheCorrectnessOfTheData(newUser);

        if (users.containsKey(newUser.getEmail())) {
            throw new InvalidCreateException("Пользователь с электронной почтой " +
                    newUser.getEmail() + " уже зарегистрирован.");
        }

        newUser.setId(newId(newUser));
        users.put(newUser.getEmail(), newUser);
        log.info("Новый пользователь: {}", newUser);

        return newUser;
    }

    @PutMapping
    public User put(@RequestBody User newUser) {

        checkingTheCorrectnessOfTheData(newUser);

        if (users.containsKey(newUser.getEmail())) {
            if (newUser.getId() != null && !newUser.getId().equals(users.get(newUser.getEmail()).getId())){
                throw new InvalidCreateException("Вы пытаетесь изменить id уже созданного пользователя. Не надо так");
            }
            newUser.setId(users.get(newUser.getEmail()).getId());
            log.info("Пользователь: {} изменен на: {}", users.get(newUser.getEmail()), newUser);
        } else {
            if (newUser.getId() == null) {
                newUser.setId(newId(newUser));
            } else {
                String email = "";
                for (User value : users.values()) {
                    if (Objects.equals(value.getId(), newUser.getId())) {
                        email = value.getEmail();
                    }
                }
                if (users.containsKey(email)){
                    users.remove(email);
                    users.put(newUser.getEmail(), newUser);
                }
            }
            log.info("Новый пользователь: {}", newUser);
        }

        users.put(newUser.getEmail(), newUser);

        return newUser;
    }

    private int id = 1;

    protected int newId(User newUser) {

        if (newUser.getId() != null && id == newUser.getId() - 1) {
            return id += 2;
        }
        if (newUser.getId() != null && id == newUser.getId()) {
            return ++id;
        }
        return id++;
    }

    private void checkingTheCorrectnessOfTheData(User newUser) {

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            throw new InvalidCreateException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new InvalidCreateException("логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new InvalidCreateException("дата рождения не может быть в будущем");
        }

        for (User value : users.values()) {
            if (value.getLogin().equals(newUser.getLogin())) {
                throw new InvalidCreateException("Пользователь с таким логином " +
                        newUser.getLogin() + " уже зарегистрирован.");
            }
        }
    }
}
