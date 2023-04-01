package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface FilmStorage {

    @NotNull
    Map<Integer, Film> findAll();

    Film add(Film film);

    Film findById(Integer id);

    Film update(Film film);

    void delete(Integer id);

    void addLikes(int filmId, int userId);

    void deleteLikes(int filmId, int userId);

    List<Film> findPopularFilms(Integer count);
}
