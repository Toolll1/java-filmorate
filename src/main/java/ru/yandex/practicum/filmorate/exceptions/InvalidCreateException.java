package ru.yandex.practicum.filmorate.exceptions;

public class InvalidCreateException extends RuntimeException{
    public InvalidCreateException(String message) {
        super(message);
    }
}
