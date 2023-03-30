package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public void deleteLikes(int filmId, int userId) {

        jdbcTemplate.update("delete from film_likes where user_id = ? and film_id = ?", userId, filmId);
    }

    @Override
    public void addLikes(int filmId, int userId) {

        jdbcTemplate.update("insert into film_likes(film_id, user_id) values (?, ?)", filmId, userId);
    }

    @Override
    public Film add(Film film) {

        String sqlQuery = "insert into films(film_id, name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery1 = "insert into film_genre(genre_id, film_id) " +
                        "values (?, ?)";

                jdbcTemplate.update(sqlQuery1,
                        genre.getId(),
                        film.getId()
                );
            }
        }

        return findAll().get(film.getId());
    }

    @Override
    public Film findById(int id) {

        return findAll().get(id);
    }

    @Override
    public Film update(Film film) {

        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where film_id = ?";

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("insert into film_genre(genre_id, film_id)  values (?, ?)",
                        genre.getId(),
                        film.getId()
                );
            }
        }

        return findAll().get(film.getId());
    }

    @Override
    public void delete(Film film) {

        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());
        jdbcTemplate.update("delete from film_likes where film_id = ?", film.getId());
        jdbcTemplate.update("delete from films where film_id = ?", film.getId());
    }

    @Override
    public Map<Integer, Film> findAll() {

        Map<Integer, Film> mapFilms = new HashMap<>();

        String sqlQuery = "SELECT f.film_id, f.name, string_agg(distinct g.genre_id::char,',') as genre_id, f.description," +
                " f.release_date, f.duration, f.rating_id, f.ll as likes\n" +
                "FROM (SELECT f.film_id, f.name, f.description, f.release_date, f.duration, r.rating_id," +
                "COUNT (fl.film_id) AS ll\n" +
                "FROM films AS f\n" +
                "LEFT OUTER JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                "LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id \n" +
                "GROUP BY f.film_id, r.rating_id) AS f\n" +
                "LEFT OUTER JOIN film_genre AS fg ON  fg.film_id = f.film_id\n" +
                "LEFT OUTER JOIN genre AS g ON  g.genre_id = fg.genre_id\n" +
                "GROUP BY f.name, f.description, f.release_date, f.duration, f.ll, f.film_id, f.rating_id;";


        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        for (Film film : listFilms) {
            mapFilms.put(film.getId(), film);
        }

        return mapFilms;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        List<Genre> genres = new ArrayList<>();

        if (resultSet.getString("genre_id") != null) {
            String[] words = resultSet.getString("genre_id").split(",");
            for (String word : words) {
                genres.add(genreStorage.findById(Integer.valueOf(word)));
            }
        }

        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .likesCount(resultSet.getInt("likes"))
                .mpa(mpaStorage.findById(resultSet.getInt("rating_id")))
                .genres(genres)
                .build();
    }
}
