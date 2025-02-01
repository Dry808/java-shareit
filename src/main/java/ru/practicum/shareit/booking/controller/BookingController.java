package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @Valid @RequestBody BookingCreateDto bookingDto) {
        log.info("Пользователь с " + userId + " создал запрос на бронирование");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable int bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Обработка запроса на бронирование с ID=" + bookingId + " владелец c ID=" + userId);
        return bookingService.approveBooking(bookingId, approved, userId);
    }


    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable int bookingId,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение данных о бронировании с ID=" + bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllBooking(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех бронированией пользователя с ID=" + userId);
        return bookingService.getAll(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getAllByOwners(state, userId);
    }
    
}

