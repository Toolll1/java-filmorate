package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidCreateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<String, Film> films = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {

        log.info("Количество фильмов в текущий момент: {}", films.size());

        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {

        checkingTheCorrectnessOfTheData(newFilm);

        if (films.containsKey(newFilm.getName())) {
            throw new InvalidCreateException("Фильм с таким названием " + newFilm.getName() + " уже зарегистрирован.");
        }
        if (newFilm.getId() == null) {
            newFilm.setId(newId(newFilm));
        }

        films.put(newFilm.getName(), newFilm);
        log.info("Новый фильм: {}", newFilm);

        return newFilm;
    }

    @PutMapping
    public Film put(@RequestBody Film newFilm) {

        checkingTheCorrectnessOfTheData(newFilm);

        if (films.containsKey(newFilm.getName())) {
            if (newFilm.getId() != null && !newFilm.getId().equals(films.get(newFilm.getName()).getId())){
                throw new InvalidCreateException("Вы пытаетесь изменить id уже созданного фильма. Не надо так");
            }
            newFilm.setId(films.get(newFilm.getName()).getId());
            log.info("Фильм: {} изменен на: {}", films.get(newFilm.getName()), newFilm);
        } else {
            if (newFilm.getId() == null) {
                newFilm.setId(newId(newFilm));
            } else {
                String name = "";
                for (Film value : films.values()) {
                    if (Objects.equals(value.getId(), newFilm.getId())) {
                        name = value.getName();
                    }
                }
                if (films.containsKey(name)){
                    films.remove(name);
                    films.put(newFilm.getName(), newFilm);
                }
            }
            log.info("Новый пользователь: {}", newFilm);
        }

        films.put(newFilm.getName(), newFilm);

        return newFilm;
    }

    private int id = 1;

    protected int newId(Film newFilm) {
        if (newFilm.getId() != null && id == newFilm.getId()-1){
            return id+=2;
        }
        return id++;
    }

    private void checkingTheCorrectnessOfTheData(Film newFilm) {

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            throw new InvalidCreateException("название не может быть пустым");
        }
        if (newFilm.getDescription().length() > 200) {
            throw new InvalidCreateException("максимальная длина описания — 200 символов");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new InvalidCreateException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (newFilm.getDuration() < 0) {
            throw new InvalidCreateException("продолжительность фильма должна быть положительной");
        }
    }
}
