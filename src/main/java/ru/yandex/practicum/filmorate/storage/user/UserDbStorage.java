package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

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
    public void deleteAllData() {

        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM rating");
        jdbcTemplate.update("DELETE FROM genre");

    }

    @Override
    public void deleteFriends(int id, int friendId) {

        String sqlQuery = "delete from user_friends where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }


    @Override
    public void addToFriends(int id, int friendId) {

        String sqlQuery = "insert into user_friends(user_id, friend_id) " +
                "values (?, ?)";

        jdbcTemplate.update(sqlQuery,
                id,
                friendId);
    }


    @Override
    public void add(User user) {

        String sqlQuery = "insert into users(user_id, name, email, login, birthday) " +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
    }

    @Override
    public Map<Integer, User> findAll() {

        Map<Integer, User> mapUsers = new HashMap<>();
        String sqlQuery = "select user_id, name, email, login, birthday from users";
        List<User> listUsers;

        try {
            listUsers = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
            for (User user : listUsers) {
                mapUsers.put(user.getId(), user);
            }
        } catch (Exception ignored) {
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
    public void update(User user) {

        String sqlQuery = "update users set " +
                "name = ?, email = ?, login = ?, birthday = ?" +
                "where user_id = ?";

        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getEmail()
                , user.getLogin()
                , user.getBirthday()
                , user.getId());
    }

    @Override
    public void delete(User user) {

        String sqlQuery = "delete from users where user_id = ?";

        jdbcTemplate.update(sqlQuery, user.getId());
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
