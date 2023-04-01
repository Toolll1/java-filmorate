package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

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

        Film film = filmStorage.add(newFilm);

        log.info("Added a new movie: {}", film);

        return film;
    }

    public Film put(Film newFilm) {

        if (newFilm.getId() == null) {
            Film film = filmStorage.add(newFilm);
            log.info("Added a new movie: {}", film);
            return film;
        } else if (filmStorage.findAll().containsKey(newFilm.getId())) {
            Film film = filmStorage.update(newFilm);
            log.info("Фильм: {} изменен на: {}", filmStorage.findById(newFilm.getId()), film);
            return film;
        } else {
            throw new ObjectNotFoundException("The movie with this id is not in the list.");
        }
    }

    public void addLikes(int filmId, int userId) {

        filmStorage.addLikes(filmId, userId);

        log.info("I received a request to add a like from a user with id {} to a movie with id {}", userId, filmId);
    }

    public void deleteLikes(int filmId, int userId) {

        filmStorage.deleteLikes(filmId, userId);

        userService.isExist(userId);

        log.info("I received a request to delete like from a user with id {} to a movie with id {}", userId, filmId);
    }

    public void deleteFilm(int id) {

        filmStorage.delete(id);

        log.info("a request was received to delete a user with id {}", id);
    }

    public List<Film> findPopularFilms(Integer count) {

        return filmStorage.findPopularFilms(count);

    }
}
