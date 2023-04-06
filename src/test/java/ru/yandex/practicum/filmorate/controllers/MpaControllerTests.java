package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTests {

    private final MpaController mpaController;

    @Test
    public void findAll_returnsTheCorrectRating_underNormalConditions() {

        assertEquals(mpaController.findAll().size(), 5);
        assertEquals(new ArrayList<>(mpaController.findAll()), new ArrayList<>(List.of(
                Mpa.builder().id(1).name("G").build(),
                Mpa.builder().id(2).name("PG").build(),
                Mpa.builder().id(3).name("PG-13").build(),
                Mpa.builder().id(4).name("R").build(),
                Mpa.builder().id(5).name("NC-17").build()
        )));
    }


    @Test
    public void findById_returnsTheCorrectRating_underNormalConditions() {

        assertEquals(mpaController.findById(1), new Mpa(1, "G"));
    }
}
