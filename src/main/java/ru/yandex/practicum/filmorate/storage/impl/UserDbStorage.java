package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
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
    public void deleteFriends(int id, int friendId) {

        jdbcTemplate.update("delete from user_friends where user_id = ? and friend_id = ?", id, friendId);
    }


    @Override
    public void addToFriends(int id, int friendId) {

        jdbcTemplate.update("insert into user_friends(user_id, friend_id) values (?, ?)", id, friendId);
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
    public void update(User user) {

        String sqlQuery = "update users set name = ?, email = ?, login = ?, birthday = ? where user_id = ?";

        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getEmail()
                , user.getLogin()
                , user.getBirthday()
                , user.getId());
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
