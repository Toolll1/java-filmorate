package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {

        return genreStorage.findAll().values();
    }

    public Genre findById(Integer id) {

        Genre genre = genreStorage.findById(id);

        if (genre == null) {
            throw new ObjectNotFoundException("The genre with this id is not in the list.");
        }

        return genre;
    }
}
