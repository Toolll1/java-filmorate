package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.InvalidCreateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {
    FilmController filmController = new FilmController();

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

        createWithAnIncorrectName();
        createWithAnIncorrectDescription();
        createWithAnIncorrectReleaseDate();
        createWithAnIncorrectDuration();

        assertEquals(filmController.findAll().size(), 0);
    }

    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheDescription() {

        //максимальная длина описания — 200 символов;
        try {
            filmController.create(Film.builder()  //длина описания — 200 символ
                    .name("rth")
                    .duration(100)
                    .description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
                            "euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisis enim ad minim " +
                            "veniam, quis nostrud exerci tati")
                    .releaseDate(LocalDate.of(2010, 12, 10))
                    .build());

        } catch (InvalidCreateException ignored) {
        }

        assertEquals(filmController.findAll().size(), 1);
    }


    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        try {
            filmController.create(Film.builder()
                    .name("rth")
                    .duration(100)
                    .description("tb")
                    .releaseDate(LocalDate.of(1895, 12, 28))
                    .build());

        } catch (InvalidCreateException ignored) {
        }

        assertEquals(filmController.findAll().size(), 1);
    }

    @Test
    public void create_returnsTheCorrectListOfFilms_onBoundaryConditionsOfTheDuration() {

        //продолжительность фильма должна быть положительной;
        try {
            filmController.create(Film.builder()
                    .name("rth")
                    .duration(0)
                    .description("tb")
                    .releaseDate(LocalDate.of(1895, 12, 27))
                    .build());

        } catch (InvalidCreateException ignored) {
        }

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

    private void createWithAnIncorrectName() {

        //название не может быть пустым
        try {
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

        } catch (InvalidCreateException ignored) {
        }
    }

    private void createWithAnIncorrectDescription() {

        //максимальная длина описания — 200 символов;
        try {
            filmController.create(Film.builder()  //длина описания — 201 символ
                    .name("rth")
                    .duration(100)
                    .description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh " +
                            "euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisis enim ad minim " +
                            "veniam, quis nostrud exerci tatio")
                    .releaseDate(LocalDate.of(2010, 12, 10))
                    .build());

        } catch (InvalidCreateException ignored) {
        }
    }

    private void createWithAnIncorrectReleaseDate() {

        //дата релиза — не раньше 28 декабря 1895 года;
        try {
            filmController.create(Film.builder()
                    .name("rth")
                    .duration(100)
                    .description("tb")
                    .releaseDate(LocalDate.of(1895, 12, 27))
                    .build());

        } catch (InvalidCreateException ignored) {
        }
    }

    private void createWithAnIncorrectDuration() {

        //продолжительность фильма должна быть положительной;
        try {
            filmController.create(Film.builder()
                    .name("rth")
                    .duration(-1)
                    .description("tb")
                    .releaseDate(LocalDate.of(1895, 12, 27))
                    .build());

        } catch (InvalidCreateException ignored) {
        }
    }
}
