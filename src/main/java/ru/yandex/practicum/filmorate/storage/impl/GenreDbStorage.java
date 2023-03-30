package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Genre> findAll() {

        Map<Integer, Genre> mapGenre = new HashMap<>();
        List<Genre> listGenre = jdbcTemplate.query("select * from genre", this::mapRowToGenre);
        for (Genre genre : listGenre) {
            mapGenre.put(genre.getId(), genre);
        }

        return mapGenre;
    }

    @Override
    public Genre findById(Integer id) {

        return findAll().get(id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {

        return Genre.builder().id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name")).build();
    }
}
