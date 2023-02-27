package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {

    Set<Integer> friends;
    private Integer id;
    private String name;
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    @Past
    private final LocalDate birthday;

}
