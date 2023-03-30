package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface FilmStorage {

    @NotNull
    Map<Integer, Film> findAll();

    Film add(Film film);

    Film findById(int id);

    Film update(Film film);

    void delete(Film film);

    void addLikes(int filmId, int userId);

    void deleteLikes(int filmId, int userId);
}
