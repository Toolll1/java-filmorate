package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTests {

    private final GenreController genreController;

    @Test
    public void findAll_returnsTheCorrectGenre_underNormalConditions() {

        assertEquals(genreController.findAll().size(), 6);
        assertEquals(new ArrayList<>(genreController.findAll()), new ArrayList<>(List.of(
                Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(),
                Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(4).name("Триллер").build(),
                Genre.builder().id(5).name("Документальный").build(),
                Genre.builder().id(6).name("Боевик").build()
        )));
    }

    @Test
    public void findById_returnsTheCorrectGenre_underNormalConditions() {

        assertEquals(genreController.findById(1), new Genre(1, "Комедия"));
    }
}
