package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {

        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {

        validate(newFilm);
        newFilm.setId(newId());
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен новый фильм: {}", newFilm);

        return newFilm;
    }

    @PutMapping
    public Film put(@RequestBody Film newFilm) {

        validate(newFilm);

        if (newFilm.getId() == null) {
            newFilm.setId(newId());
            films.put(newFilm.getId(), newFilm);
            log.info("Добавлен новый фильм: {}", newFilm);
        } else if (films.containsKey(newFilm.getId())) {
            log.info("Фильм: {} изменен на: {}", films.get(newFilm.getId()), newFilm);
            films.put(newFilm.getId(), newFilm);
        } else {
            throw new ValidateException("Фильма с таким id нет в списке.");
        }

        return newFilm;
    }

    private int id = 1;

    protected int newId() {
        return id++;
    }

    private void validate(Film newFilm) {

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new ValidateException("название не может быть пустым");
        }
        if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
            throw new ValidateException("максимальная длина описания — 200 символов");
        } else if (newFilm.getDescription() == null || newFilm.getDescription().isBlank()) {
            throw new ValidateException("описание не заполнено");
        }
        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("дата релиза — не раньше 28 декабря 1895 года");
        } else if (newFilm.getReleaseDate() == null) {
            throw new ValidateException("дата релиза не заполнена");
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() < 0) {
            throw new ValidateException("продолжительность фильма должна быть положительной");
        } else if (newFilm.getDuration() == null) {
            throw new ValidateException("продолжительность не заполнена");
        }
    }
}
