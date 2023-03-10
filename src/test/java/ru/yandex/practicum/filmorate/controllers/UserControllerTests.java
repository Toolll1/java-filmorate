package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTests {

    UserController userController = new UserController();

    @Test
    public void findAll_returnsTheCorrectListOfUsers_underNormalConditions() {

        userController.create(User.builder()
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        assertEquals(userController.findAll().size(), 1);
    }

    @Test
    public void create_returnsTheCorrectListOfUsers_withAnIncorrectData() {

        assertThrows(ValidateException.class, this::incorrectEmail);
        assertThrows(ValidateException.class, this::incorrectLogin);
        assertThrows(ValidateException.class, this::incorrectBirthday);
        assertEquals(userController.findAll().size(), 0);
    }

    @Test
    public void create_returnsTheCorrectListOfUsers_inTheAbsenceOfAName() {

        //имя для отображения может быть пустым — в таком случае будет использован логин;
        userController.create(User.builder() // имя пустое
                .name("")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder() // имени нет
                .email("1ddd@ss.ru")
                .login("1tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        assertEquals(userController.findAll().size(), 2);

        for (User user : userController.findAll()) {
            assertEquals(user.getName(), user.getLogin());
        }
    }

    @Test
    public void create_returnsTheCorrectListOfUsers_onBoundaryConditionsOfTheBirthday() {

        // дата рождения не может быть в будущем;
        userController.create(User.builder() // дата рождения сегодня
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2023, 2, 12))
                .build());

        assertEquals(userController.findAll().size(), 1);
    }

    @Test
    public void put_returnsTheCorrectListOfUsers_underNormalConditions() {

        userController.create(User.builder()
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.put(User.builder()
                .id(1)
                .name("rth1")
                .email("ddd@ss.ru")
                .login("tb1")
                .birthday(LocalDate.of(2010, 12, 11))
                .build());

        User savedFilm = (User) (((Object[]) userController.findAll().toArray())[0]);

        assertEquals(userController.findAll().size(), 1);
        assertEquals(savedFilm.getName(), "rth1");
        assertEquals(savedFilm.getEmail(), "ddd@ss.ru");
        assertEquals(savedFilm.getLogin(), "tb1");
        assertEquals(savedFilm.getBirthday(), LocalDate.of(2010, 12, 11));
    }

    private void incorrectEmail() {

        //электронная почта не может быть пустой и должна содержать символ @
        userController.create(User.builder() // пустая почта
                .name("rth")
                .email("")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder()  // нет почты
                .name("rth")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder() // нет @
                .name("rth")
                .email("rgwert")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder() // неправильный email
                .name("rth")
                .email("eto-nepravilnyi?email@")
                .login("tb")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());
    }

    private void incorrectLogin() {

        //логин не может быть пустым и содержать пробелы;
        userController.create(User.builder() // логин с пробелом
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb ")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder() // пустой логин
                .name("rth")
                .email("ddd@ss.ru")
                .login("")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());

        userController.create(User.builder() // нет логина
                .name("rth")
                .email("ddd@ss.ru")
                .birthday(LocalDate.of(2010, 12, 10))
                .build());
    }

    private void incorrectBirthday() {

        // дата рождения не может быть в будущем;
        userController.create(User.builder() // дата рождения в будущем
                .name("rth")
                .email("ddd@ss.ru")
                .login("tb")
                .birthday(LocalDate.of(2030, 12, 10))
                .build());
    }
}
