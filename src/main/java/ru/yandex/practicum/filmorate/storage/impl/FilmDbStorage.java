package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    public List<Film> findPopularFilms(Integer count) {

        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "r.rating_id, count (fl.film_id) as likes\n" +
                "FROM films as f\n" +
                "left outer join film_likes as fl on f.film_id = fl.film_id\n" +
                "left outer join rating as r on f.rating_id = r.rating_id\n" +
                "group by f.film_id, r.rating_id\n" +
                "order by likes desc\n" +
                "limit ?;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public void addLikes(int filmId, int userId) {

        jdbcTemplate.update("insert into film_likes(film_id, user_id) values (?, ?)", filmId, userId);
    }

    @Override
    public Film add(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> newFilm = new HashMap<>(
                Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "rating_id", film.getMpa().getId()
                ));

        Integer id = (int) simpleJdbcInsert.executeAndReturnKey(newFilm).longValue();

        film.setId(id);

        if (film.getGenres() != null) {
            for (Genre genre : new HashSet<>(film.getGenres())) {
                String sqlQuery1 = "insert into film_genre(genre_id, film_id) " +
                        "values (?, ?)";

                jdbcTemplate.update(sqlQuery1,
                        genre.getId(),
                        film.getId()
                );
            }
        }

        return findById(film.getId());
    }

    @Override
    public Film findById(Integer id) {

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.film_id, f.name, f.description, f.release_date," +
                "f.duration, r.rating_id, count (fl.film_id) as likes\n" +
                "from films as f\n" +
                "left outer join film_likes as fl on f.film_id = fl.film_id\n" +
                "left outer join rating as r on f.rating_id = r.rating_id\n" +
                "where f.film_id = ?\n" +
                "group by f.film_id, r.rating_id;", id);

        if (filmRows.next()) {

            String sql = "select g.genre_id, g.name\n" +
                    "from genre g \n" +
                    "left outer join film_genre fg on g.genre_id = fg.genre_id \n" +
                    "where fg.film_id =?;";

            List<Genre> genres = jdbcTemplate.query(sql, (rs, rn) -> makeGenre(rs), filmRows.getInt("film_id"));

            return new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getInt("likes"),
                    mpaStorage.findById(filmRows.getInt("rating_id")),
                    genres);
        } else {
            return null;
        }
    }

    @Override
    public Film update(Film film) {

        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        jdbcTemplate.update("delete from film_genre where film_id = ?", film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : new HashSet<>(film.getGenres())) {
                jdbcTemplate.update("insert into film_genre(genre_id, film_id)  values (?, ?)",
                        genre.getId(),
                        film.getId()
                );
            }
        }

        return findById(film.getId());
    }

    @Override
    public void delete(Integer id) {

        jdbcTemplate.update("delete from film_genre where film_id = ?", id);
        jdbcTemplate.update("delete from film_likes where film_id = ?", id);
        jdbcTemplate.update("delete from films where film_id = ?", id);
    }

    @Override
    public Map<Integer, Film> findAll() {

        Map<Integer, Film> mapFilms = new HashMap<>();

        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, " +
                "f.duration, r.rating_id, count (fl.film_id) as likes\n" +
                "from films as f\n" +
                "left outer join film_likes as fl on f.film_id = fl.film_id\n" +
                "left outer join rating as r on f.rating_id = r.rating_id\n" +
                "group by f.film_id, r.rating_id;";

        List<Film> listFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        for (Film film : listFilms) {
            mapFilms.put(film.getId(), film);
        }

        return mapFilms;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        String sql = "select g.genre_id, g.name\n" +
                "from genre g \n" +
                "left outer join film_genre fg on g.genre_id = fg.genre_id \n" +
                "where fg.film_id =?;";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rn) -> makeGenre(rs), resultSet.getInt("film_id"));

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

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
