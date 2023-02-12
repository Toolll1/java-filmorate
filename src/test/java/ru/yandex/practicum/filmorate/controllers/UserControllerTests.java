package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTests {

    UserController userController = new UserController();

    @Test
    public void findAll_returnsTheCorrectListOfUsers_underNormalConditions() {

        userController.create(User.builder()
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2010,12,10))
                .build());

        assertEquals(userController.findAll().size(), 1);
    }
}
