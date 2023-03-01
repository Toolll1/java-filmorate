package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface FilmStorage {

    @NotNull
    Map<Integer, Film> findAll();

    void add(Film film);

    Film findById(int id);

    void update(Film film);

    void delete(Film film);
}
