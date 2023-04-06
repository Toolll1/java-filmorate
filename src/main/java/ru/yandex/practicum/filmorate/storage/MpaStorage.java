package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;

public interface MpaStorage {
    Map<Integer, Mpa> findAll();

    Mpa findById(Integer id);
}
