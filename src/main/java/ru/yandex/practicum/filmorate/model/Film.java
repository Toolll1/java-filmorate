package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Film {

    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    @Past
    private LocalDate releaseDate;
    private Integer duration;
    private Integer likesCount;
    @NotNull
    private Mpa mpa;
    List<Genre> genres = new ArrayList<>();
}
