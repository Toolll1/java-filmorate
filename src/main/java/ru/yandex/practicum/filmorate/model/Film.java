package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {

    private Integer id;
    private Set<User> likes;
    @NotNull
    @NotBlank
    private final String name;
    private final String description;
    @Past
    private final LocalDate releaseDate;
    private final Integer duration;

}
