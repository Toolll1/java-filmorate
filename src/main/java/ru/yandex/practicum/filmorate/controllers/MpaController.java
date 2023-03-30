package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService service;

    @GetMapping
    public Collection<Mpa> findAll() {

        Collection<Mpa> mpa = service.findAll();
        log.debug("Получен список рейтингов: {} ", mpa);
        return mpa;
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable Integer id) {

        Mpa mpa = service.findById(id);
        log.debug("Получен рейтинг с идентификатором: {} ", id);
        return mpa;
    }
}
