package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTests {

    private final FilmController filmController;
    private final UserService userService;

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectMovie_underNormalConditions() {

        Film film = Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .genres(new ArrayList<>(List.of(Genre.builder()
                                .id(1)
                                .build(),
                        Genre.builder()
                                .id(2)
                                .build())))
                .mpa(new Mpa(1, "G"))
                .build();

        filmController.create(film);

        assertEquals(filmController.findById(1).getId(), film.getId());
        assertEquals(filmController.findById(1).getReleaseDate(), film.getReleaseDate());
        assertEquals(filmController.findById(1).getDescription(), film.getDescription());
        assertEquals(filmController.findById(1).getDuration(), film.getDuration());
        assertEquals(filmController.findById(1).getName(), film.getName());
        assertEquals(filmController.findById(1).getMpa().getId(), film.getMpa().getId());
        assertEquals(filmController.findById(1).getMpa().getName(), film.getMpa().getName());
        assertEquals(filmController.findById(1).getGenres().size(), film.getGenres().size());
        assertEquals(filmController.findById(1).getGenres().get(0).getId(), film.getGenres().get(0).getId());
        assertEquals(filmController.findById(1).getGenres().get(1).getId(), film.getGenres().get(1).getId());
        assertEquals(filmController.findById(1).getGenres().get(0).getName(), "Комедия");
        assertEquals(filmController.findById(1).getGenres().get(1).getName(), "Драма");
    }

    @DirtiesContext
    @Test
    public void findPopularFilms_returnsTheCorrectListOfMovies_underNormalConditions() {

        creatingThreeFilm();
        creatingThreeUsers();

        filmController.addLikes(1, 1);
        filmController.addLikes(3, 2);
        filmController.addLikes(3, 3);
        filmController.addLikes(2, 1);
        filmController.addLikes(2, 2);
        filmController.addLikes(2, 3);

        List<Film> popularFilms = filmController.findPopularFilms(6);

        assertEquals(popularFilms.get(0).getId(), 2);
        assertEquals(popularFilms.size(), 3);

        List<Film> popularFilms1 = filmController.findPopularFilms(2);
        assertEquals(popularFilms1.get(0).getId(), 2);
        assertEquals(popularFilms1.size(), 2);
    }

    @DirtiesContext
    @Test
    public void addLikes_returnsTheCorrectListOfLikes_underNormalConditions() {

        creatingThreeFilm();
        creatingThreeUsers();

        filmController.addLikes(2, 1);
        filmController.addLikes(2, 2);
        filmController.addLikes(2, 3);

        assertEquals(filmController.findById(2).getLikesCount(), 3);
    }

    @DirtiesContext
    @Test
    public void deleteLikes_returnsTheCorrectListOfLikes_underNormalConditions() {

        creatingThreeFilm();
        creatingThreeUsers();

        filmController.addLikes(1, 1);
        assertEquals(filmController.findById(1).getLikesCount(), 1);

        filmController.deleteLikes(1, 1);
        assertEquals(filmController.findById(1).getLikesCount(), 0);
    }

    @DirtiesContext
    @Test
    public void deleteFilm_returnsTheCorrectListOfFilms_underNormalConditions() {

        Film film = Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .genres(new ArrayList<>(List.of(Genre.builder()
                        .id(1)
                        .build())))
                .mpa(new Mpa(1, "G"))
                .build();

        filmController.create(film);
        assertEquals(filmController.findAll().size(), 1);

        filmController.deleteFilm(1);
        assertEquals(filmController.findAll().size(), 0);
    }

    private void creatingThreeUsers() {

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

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
    }

    private void creatingThreeFilm() {

        Film film = Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .genres(new ArrayList<>(List.of(Genre.builder()
                        .id(1)
                        .build())))
                .mpa(new Mpa(1, "G"))
                .build();

        filmController.create(film);
        filmController.create(film);
        filmController.create(film);
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectListOfMovies_underNormalConditions() {

        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());

        assertEquals(filmController.findAll().size(), 1);


    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectListOfFilms_withAnIncorrectData() {

        assertThrows(ValidateException.class, this::incorrectName);
        assertThrows(ValidateException.class, this::incorrectDescription);
        assertThrows(ValidateException.class, this::incorrectReleaseDate);
        assertThrows(ValidateException.class, this::incorrectDuration);
        assertEquals(filmController.findAll().size(), 0);
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheDescription() {

        //максимальная длина описания — 200 символов;
        filmController.create(Film.builder()  //длина описания — 200 символ
                .name("rth")
                .duration(100)
                .description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
                        "euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisis enim ad minim " +
                        "veniam, quis nostrud exerci tati")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .mpa(new Mpa(1, "G"))
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheDuration() {

        //продолжительность фильма должна быть положительной;
        Film film = Film.builder()
                .name("rth")
                .duration(0)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(new Mpa(1, "G"))
                .build();

        assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals(filmController.findAll().size(), 0);
    }

    @DirtiesContext
    @Test
    public void put_returnsTheCorrectListOfMovies_underNormalConditions() {

        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());

        filmController.put(Film.builder()
                .id(1)
                .name("rth")
                .duration(120)
                .description("tb1")
                .releaseDate(LocalDate.of(2010, 12, 11))
                .mpa(new Mpa(1, "G"))
                .build());

        Film savedFilm = (Film) (((Object[]) filmController.findAll().toArray())[0]);

        assertEquals(filmController.findAll().size(), 1);
        assertEquals(savedFilm.getName(), "rth");
        assertEquals(savedFilm.getDuration(), 120);
        assertEquals(savedFilm.getDescription(), "tb1");
        assertEquals(savedFilm.getReleaseDate(), LocalDate.of(2010, 12, 11));
    }

    private void incorrectName() {

        //название не может быть пустым
        filmController.create(Film.builder() // пустое название
                .name("")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());

        filmController.create(Film.builder()  // нет названия
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());
    }

    private void incorrectDescription() {

        //максимальная длина описания — 200 символов;
        filmController.create(Film.builder()  //длина описания — 201 символ
                .name("rth")
                .duration(100)
                .description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
                        "euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisis enim ad minim " +
                        "veniam, quis nostrud exerci tatio")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .mpa(new Mpa(1, "G"))
                .build());
    }

    private void incorrectReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(new Mpa(1, "G"))
                .build());
    }

    private void incorrectDuration() {

        //продолжительность фильма должна быть положительной;
        filmController.create(Film.builder()
                .name("rth")
                .duration(-1)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(new Mpa(1, "G"))
                .build());
    }
}
