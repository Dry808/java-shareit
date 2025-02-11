package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Класс для обработки исключений в приложении и возвращения соответствующих HTTP-ответов.
 */

@RestControllerAdvice
public class ErrorHandler {

    // возвращает HTTP код 404(not_found)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    // возвращает HTTP код 403
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse accessDeniedException(final AccessDeniedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse bookingException(final BookingException e) {
        return new ErrorResponse(e.getMessage());
    }
}
