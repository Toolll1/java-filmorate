package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    @GetMapping
    public Collection<Genre> findAll() {

        Collection<Genre> genres = service.findAll();
        log.debug("Получен список жанров: {} ", genres);
        return genres;
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Integer id) {

        Genre genre = service.findById(id);
        log.debug("Получен жанр с идентификатором: {} ", id);
        return genre;
    }
}
