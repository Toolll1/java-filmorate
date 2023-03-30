package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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

        newFilm.setId(newId());
        log.info("Added a new movie: {}", newFilm);

        return filmStorage.add(newFilm);
    }

    public Film put(Film newFilm) {

        if (newFilm.getId() == null) {
            newFilm.setId(newId());
            log.info("Added a new movie: {}", newFilm);
            return filmStorage.add(newFilm);
        } else if (filmStorage.findAll().containsKey(newFilm.getId())) {
            log.info("Фильм: {} изменен на: {}", filmStorage.findById(newFilm.getId()), newFilm);
            return filmStorage.update(newFilm);
        } else {
            throw new ObjectNotFoundException("The movie with this id is not in the list.");
        }

    }

    public String addLikes(int filmId, int userId) {

        searchId(filmId);
        userService.searchId(userId);

        Film film = filmStorage.findById(filmId);

        filmStorage.addLikes(filmId, userId);

        return "The movie " + film + " now has " + film.getLikesCount() + " likes.";

    }

    public String deleteLikes(int filmId, int userId) {

        searchId(filmId);
        userService.searchId(userId);
        filmStorage.deleteLikes(filmId, userId);

        Film film = filmStorage.findById(filmId);

        return "The movie " + film + " now has " + film.getLikesCount() + " likes.";
    }

    public String deleteFilm(int id) {

        searchId(id);

        Film film = filmStorage.findById(id);

        filmStorage.delete(film);

        return "user with id " + id + " deleted";
    }

    public Collection<Film> findPopularFilms(Integer count) {

        return filmStorage.findAll().values().stream()
                .sorted((film1, film2) -> film2.getLikesCount() - film1.getLikesCount())
                .limit(count).collect(Collectors.toList());
    }

    public void searchId(int... ids) {

        for (int id : ids) {
            if (!filmStorage.findAll().containsKey(id)) {
                throw new ObjectNotFoundException("Id is specified incorrectly");
            }
        }
    }

    private int id = 1;

    protected int newId() {

        return id++;
    }
}
