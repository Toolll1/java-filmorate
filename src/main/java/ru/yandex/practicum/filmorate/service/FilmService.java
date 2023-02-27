package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Map<Integer, Film> findAll() {

        return filmStorage.findAll();
    }

    public Film findById(int filmId) {

        if (filmStorage.findAll().containsKey(filmId)) {
            return filmStorage.findAll().get(filmId);
        } else {
            throw new ObjectNotFoundException("Фильма с таким id нет в списке.");
        }
    }

    public Film create(Film newFilm) {

        validate(newFilm);
        newFilm.setId(newId());
        filmStorage.add(newFilm);
        log.info("Добавлен новый фильм: {}", newFilm);

        return newFilm;
    }

    public Film put(Film newFilm) {

        validate(newFilm);
        if (newFilm.getId() == null) {
            newFilm.setId(newId());
            log.info("Добавлен новый фильм: {}", newFilm);
            filmStorage.add(newFilm);
        } else if (filmStorage.findAll().containsKey(newFilm.getId())) {
            log.info("Фильм: {} изменен на: {}", filmStorage.findAll().get(newFilm.getId()), newFilm);
            filmStorage.add(newFilm);
        } else {
            throw new ObjectNotFoundException("Фильма с таким id нет в списке.");
        }

        return newFilm;
    }

    public String addLikes(int id, int userId) {

        if (!filmStorage.findAll().containsKey(id)) {
            throw new ObjectNotFoundException("Некорректно указан id фильма");
        }
        if (!userStorage.findAll().containsKey(userId)) {
            throw new ObjectNotFoundException("Некорректно указан id пользователя");
        }

        Set<User> likes;
        if (filmStorage.findAll().get(id).getLike() != null) {
            likes = filmStorage.findAll().get(id).getLike();
        } else {
            likes = new HashSet<>();
        }
        likes.add(userStorage.findAll().get(userId));

        filmStorage.findAll().get(id).setLike(likes);

        return "У фильма " + filmStorage.findAll().get(id) + " теперь "
                + filmStorage.findAll().get(id).getLike().size() + " лайков.";

    }

    public String deleteLikes(int id, int userId) {

        if (filmStorage.findAll() == null) {
            throw new ObjectNotFoundException("Список фильмов пуст");
        }
        if (userStorage.findAll() == null) {
            throw new ObjectNotFoundException("Список пользователей пуст");
        }
        if (!filmStorage.findAll().containsKey(id)) {
            throw new ObjectNotFoundException("Некорректно указан id фильма");
        }
        if (!userStorage.findAll().containsKey(userId)) {
            throw new ObjectNotFoundException("Некорректно указан id пользователя");
        }

        if (filmStorage.findAll().get(id).getLike().contains(userStorage.findAll().get(userId))) {

            Set<User> likes = filmStorage.findAll().get(id).getLike();
            likes.remove(userStorage.findAll().get(userId));

            filmStorage.findAll().get(id).setLike(likes);

            return "У фильма " + filmStorage.findAll().get(id) + " теперь "
                    + filmStorage.findAll().get(id).getLike().size() + " лайков.";
        } else {
            throw new ObjectNotFoundException("Некорректно указан id фильма или у фильма не было лайка от данного пользователя");
        }

    }

    public Collection<Film> findPopularFilms(Integer count) {

        List<Film> bestFilms = new ArrayList<>();

        if (count > filmStorage.findAll().size()) {
            count = filmStorage.findAll().size();
        }

        if (count <= 0) {
            throw new ValidateException("размер запрашиваемого списка лучших фильмов должен быть больше 0");
        } else if (filmStorage.findAll().isEmpty()) {
            throw new ObjectNotFoundException("Список фильмов пуст");
        } else {
            List<Film> ratedFilms = new ArrayList<>();

            for (Film film : filmStorage.findAll().values()) {
                if (film.getLike() != null && !film.getLike().isEmpty()) {
                    ratedFilms.add(film);
                }
            }

            if (!ratedFilms.isEmpty()) {
                bestFilms = ratedFilms.stream().sorted(
                        (film1, film2) -> film2.getLike().size() - film1.getLike().size()).collect(Collectors.toList());
            }

            for (Film film : filmStorage.findAll().values()) {
                if (film.getLike() == null || film.getLike().isEmpty()) {
                    bestFilms.add(film);
                }
                bestFilms = bestFilms.stream().limit(count).collect(Collectors.toList());
            }
        }

        return bestFilms;
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
        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(
                LocalDate.of(1895, 12, 28))) {
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
