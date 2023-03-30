package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {

        return userService.findAll();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> mutualFriends(@PathVariable int id, @PathVariable int otherId) {

        return userService.mutualFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriendsById(@PathVariable int id) {

        return userService.findFriendsById(id);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {

        return userService.findById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {

        validate(newUser);

        return userService.create(newUser);
    }

    @PutMapping
    public User put(@Valid @RequestBody User newUser) {

        validate(newUser);

        return userService.put(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addToFriends(@PathVariable int id, @PathVariable int friendId) {

        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public String deleteFriends(@PathVariable int id, @PathVariable int friendId) {

        return userService.deleteFriends(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable int userId) {

        return userService.deleteUser(userId);
    }

    private void validate(User newUser) {

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            throw new ValidateException("the email cannot be empty and must contain the character @");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            throw new ValidateException("the login cannot be empty and contain spaces");
        }
        if (newUser.getBirthday() != null && newUser.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("the date of birth cannot be in the future");
        } else if (newUser.getBirthday() == null) {
            throw new ValidateException("date of birth not filled in");
        }
    }
}
