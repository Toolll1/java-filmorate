package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;

import java.util.Map;

@ControllerAdvice(assignableTypes = {FilmController.class, UserController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final ValidateException e) {

        return new ResponseEntity<>(
                Map.of("Ошибка валидации: ", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleObjectNotFound(final ObjectNotFoundException e) {

        return new ResponseEntity<>(
                Map.of("id не найден.", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleRemainingErrors(final RuntimeException e) {

        return new ResponseEntity<>(
                Map.of("ошибка: ", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
