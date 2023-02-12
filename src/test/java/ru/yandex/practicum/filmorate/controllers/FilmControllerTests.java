package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {
    FilmController filmController = new FilmController();

    @Test
    public void findAll_returnsTheCorrectListOfMovies_underNormalConditions() {

        filmController.create(Film.builder()
                .name("rth").duration(100)
                .description("tb")
                .releaseDate(LocalDate.of(2010,12,10))
                .build());

        assertEquals(filmController.findAll().size(), 1);
    }





}
