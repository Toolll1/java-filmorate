package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTests {

    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();
    FilmService filmService = new FilmService(filmStorage, userStorage);
    FilmController filmController = new FilmController(filmService);

    @Test
    public void findAll_returnsTheCorrectListOfMovies_underNormalConditions() {

        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }

    @Test
    public void create_returnsTheCorrectListOfFilms_withAnIncorrectData() {

        assertThrows(ValidateException.class, this::incorrectName);
        assertThrows(ValidateException.class, this::incorrectDescription);
        assertThrows(ValidateException.class, this::incorrectReleaseDate);
        assertThrows(ValidateException.class, this::incorrectDuration);
        assertEquals(filmController.findAll().size(), 0);
    }

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
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }


    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }

    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheDuration() {

        //продолжительность фильма должна быть положительной;
        Film film = Film.builder()
                .name("rth")
                .duration(0)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals(filmController.findAll().size(), 0);
    }

    @Test
    public void put_returnsTheCorrectListOfMovies_underNormalConditions() {

        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
                .build());

        filmController.put(Film.builder()
                .id(1)
                .name("rth")
                .duration(120)
                .description("tb1")
                .releaseDate(LocalDate.of(2010, 12, 11))
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
                .build());

        filmController.create(Film.builder()  // нет названия
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010, 12, 10))
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
                .build());
    }

    private void incorrectReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        filmController.create(Film.builder()
                .name("rth")
                .duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build());
    }

    private void incorrectDuration() {

        //продолжительность фильма должна быть положительной;
        filmController.create(Film.builder()
                .name("rth")
                .duration(-1)
                .description("tb")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build());
    }
}
