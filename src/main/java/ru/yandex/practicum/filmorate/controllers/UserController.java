package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {

        return userService.findAll().values();
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

        return userService.create(newUser);
    }

    @PutMapping
    public User put(@Valid @RequestBody User newUser) {

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
}
