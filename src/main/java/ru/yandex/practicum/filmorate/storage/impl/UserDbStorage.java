package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Integer> findFriendsById(int id) {

        Set<Integer> result = new HashSet<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select friend_id from user_friends where user_id=?", id);

        while (userRows.next()) {
            result.add(userRows.getInt("friend_id"));
        }

        return result;
    }

    @Override
    public List<User> mutualFriends(int id, int otherId) {

        return jdbcTemplate.query("select * from users where user_id in " +
                        "(select friend_id from user_friends where user_id = ? and friend_id in " +
                        "(select friend_id from user_friends where user_id = ?))",
                this::mapRowToUser, id, otherId);
    }

    @Override
    public void deleteFriends(int id, int friendId) {

        jdbcTemplate.update("delete from user_friends where user_id = ? and friend_id = ?", id, friendId);
    }

    @Override
    public void addToFriends(int id, int friendId) {

        jdbcTemplate.update("insert into user_friends(user_id, friend_id) values (?, ?)", id, friendId);
    }

    @Override
    public User add(User user) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> newFilm = new HashMap<>(
                Map.of("name", user.getName(),
                        "email", user.getEmail(),
                        "login", user.getLogin(),
                        "birthday", user.getBirthday()
                ));

        Integer id = (int) simpleJdbcInsert.executeAndReturnKey(newFilm).longValue();

        user.setId(id);

        return findById(user.getId());
    }

    @Override
    public Map<Integer, User> findAll() {

        Map<Integer, User> mapUsers = new HashMap<>();
        List<User> listUsers = jdbcTemplate.query("select user_id, name, email, login, birthday from users",
                this::mapRowToUser);

        for (User user : listUsers) {
            mapUsers.put(user.getId(), user);
        }

        return mapUsers;
    }

    @Override
    public User findById(int id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {
            return new User(
                    userRows.getInt("user_id"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getDate("birthday").toLocalDate());
        } else {
            return null;
        }
    }

    @Override
    public User update(User user) {

        String sqlQuery = "update users set name = ?, email = ?, login = ?, birthday = ? where user_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());

        return findById(user.getId());
    }

    @Override
    public void delete(User user) {

        jdbcTemplate.update("delete from user_friends where user_id = ?", user.getId());
        jdbcTemplate.update("delete from film_likes where user_id = ?", user.getId());
        jdbcTemplate.update("delete from users where user_id = ?", user.getId());
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
