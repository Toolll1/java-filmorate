package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {

        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable int filmId) {

        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {

        if (count <= 0 || filmService.findAll().isEmpty()) {
            return new ArrayList<>();
        }

        return filmService.findPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {

        validate(newFilm);

        return filmService.create(newFilm);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film newFilm) {

        validate(newFilm);

        return filmService.put(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikes(@PathVariable int id, @PathVariable int userId) {

        filmService.addLikes(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikes(@PathVariable int id, @PathVariable int userId) {

        filmService.deleteLikes(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {

        filmService.deleteFilm(id);
    }

    private void validate(Film newFilm) {

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidateException("the name cannot be empty");
        }
        if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
            throw new ValidateException("the maximum length of the description is 200 characters");
        } else if (newFilm.getDescription() == null || newFilm.getDescription().isBlank()) {
            throw new ValidateException("description is not filled in");
        }
        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("release date — no earlier than December 28, 1895");
        } else if (newFilm.getReleaseDate() == null) {
            throw new ValidateException("the release date is not filled in");
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() < 0) {
            throw new ValidateException("the duration of the film should be positive");
        } else if (newFilm.getDuration() == null) {
            throw new ValidateException("duration not filled");
        }
    }
}
