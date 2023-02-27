package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {

        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {

        return filmService.findAll().values();
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable int filmId) {

        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {

        return filmService.findPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {

        return filmService.create(newFilm);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film newFilm) {

        return filmService.put(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLikes(@PathVariable int id, @PathVariable int userId) {

        return filmService.addLikes(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public String deleteLikes(@PathVariable int id, @PathVariable int userId) {

        return filmService.deleteLikes(id, userId);
    }
}
