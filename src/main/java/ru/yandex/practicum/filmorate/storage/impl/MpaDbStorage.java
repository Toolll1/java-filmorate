package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Mpa> findAll() {

        Map<Integer, Mpa> mapMpa = new HashMap<>();
        String sqlQuery = "select * from rating";
        List<Mpa> listMpa = jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        for (Mpa mpa : listMpa) {
            mapMpa.put(mpa.getId(), mpa);
        }

        return mapMpa;
    }

    @Override
    public Mpa findById(Integer id) {

        return findAll().get(id);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
