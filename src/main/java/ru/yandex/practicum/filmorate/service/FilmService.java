package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {

        return filmStorage.findAll().values();
    }

    public Film findById(int filmId) {

        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new ObjectNotFoundException("The movie with this id is not in the list.");
        }

        return film;
    }

    public Film create(Film newFilm) {

        if (newFilm.getLikes() == null) {
            newFilm.setLikes(new HashSet<>());
        }

        newFilm.setId(newId());
        filmStorage.add(newFilm);
        log.info("Added a new movie: {}", newFilm);

        return newFilm;
    }

    public Film put(Film newFilm) {

        if (newFilm.getLikes() == null) {
            newFilm.setLikes(new HashSet<>());
        }
        if (newFilm.getId() == null) {
            newFilm.setId(newId());
            log.info("Added a new movie: {}", newFilm);
            filmStorage.add(newFilm);
        } else if (filmStorage.findAll().containsKey(newFilm.getId())) {
            log.info("Фильм: {} изменен на: {}", filmStorage.findById(newFilm.getId()), newFilm);
            if (newFilm.getLikes() == null) newFilm.setLikes(new HashSet<>());
            filmStorage.update(newFilm);
        } else {
            throw new ObjectNotFoundException("The movie with this id is not in the list.");
        }

        return newFilm;
    }

    public String addLikes(int filmId, int userId) {

        Film film = filmStorage.findById(filmId);
        User user = userService.findById(userId);

        film.getLikes().add(user);

        return "The movie " + film + " now has " + film.getLikes().size() + " likes.";

    }

    public String deleteLikes(int filmId, int userId) {

        Film film = filmStorage.findById(filmId);
        User user = userService.findById(userId);

        film.getLikes().remove(user);

        return "The movie " + film + " now has " + film.getLikes().size() + " likes.";
    }

    public Collection<Film> findPopularFilms(Integer count) {

        return filmStorage.findAll().values()
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private int id = 1;

    protected int newId() {

        return id++;
    }
}
