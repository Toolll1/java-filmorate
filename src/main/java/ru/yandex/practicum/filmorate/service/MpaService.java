package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {

        return mpaStorage.findAll().values();
    }

    public Mpa findById(Integer id) {

        Mpa mpa = mpaStorage.findById(id);

        if (mpa == null) {
            throw new ObjectNotFoundException("The rating with id {} is not in the list." + id);
        }

        return mpa;
    }
}
