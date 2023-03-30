package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {

    private final UserController userController;

    @DirtiesContext
    @Test
    public void findFriendsById_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();

        User user2 = User.builder()
                .name("name2")
                .email("email2@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();

        userController.create(user1);
        userController.create(user2);
        userController.addToFriends(1, 2);
        assertEquals(userController.findFriendsById(1), List.of(user2));
    }

    @DirtiesContext
    @Test
    public void mutualFriends_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();
        User user2 = User.builder()
                .name("name2")
                .email("email2@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();
        User user3 = User.builder()
                .name("name3")
                .email("email3@mail.ru")
                .login("login3")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();

        userController.create(user1);
        userController.create(user2);
        userController.create(user3);
        userController.addToFriends(1, 2);
        userController.addToFriends(3, 2);

        assertEquals(userController.mutualFriends(1, 3), List.of(user2));
    }

    @DirtiesContext
    @Test
    public void addToFriends_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();
        User user2 = User.builder()
                .name("name2")
                .email("email2@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();


        userController.create(user1);
        userController.create(user2);

        userController.addToFriends(1, 2);

        assertEquals(userController.findFriendsById(1), List.of(user2));

    }

    @DirtiesContext
    @Test
    public void deleteFriends_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();
        User user2 = User.builder()
                .name("name2")
                .email("email2@mail.ru")
                .login("login2")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();


        userController.create(user1);
        userController.create(user2);

        userController.addToFriends(1, 2);

        assertEquals(userController.findFriendsById(1), List.of(user2));

        userController.deleteFriends(1, 2);

        assertEquals(userController.findFriendsById(1), new ArrayList<>());

    }

    @DirtiesContext
    @Test
    public void deleteUser_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();

        userController.create(user1);

        assertEquals(userController.findAll(), List.of(user1));

        userController.deleteUser(1);

        assertEquals(userController.findAll(), new ArrayList<>());
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectListOfUsers_underNormalConditions() {

        User user1 = User.builder()
                .name("name1")
                .email("email1@mail.ru")
                .login("login1")
                .birthday(LocalDate.of(2010, 12, 10))
                .build();

        userController.create(user1);
        assertEquals(userController.findById(1), user1);
    }

    @DirtiesContext
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

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectListOfUsers_withAnIncorrectData() {

        assertThrows(ValidateException.class, this::incorrectEmail);
        assertThrows(ValidateException.class, this::incorrectLogin);
        assertThrows(ValidateException.class, this::incorrectBirthday);
        assertEquals(userController.findAll().size(), 0);
    }

    @DirtiesContext
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

    @DirtiesContext
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

    @DirtiesContext
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
