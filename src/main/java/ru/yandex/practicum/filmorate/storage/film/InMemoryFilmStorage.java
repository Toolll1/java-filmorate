package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @Override
    public Map<Integer, Film> findAll() {

        return films;
    }

    @Override
    public void add(Film film) {

        films.put(film.getId(), film);
    }

    @Override
    public Film findById(int id) {

        return films.get(id);
    }

    @Override
    public void update(Film film) {

        films.put(film.getId(), film);
    }

    @Override
    public void delete(Film film) {

        films.remove(film.getId());
    }
}
