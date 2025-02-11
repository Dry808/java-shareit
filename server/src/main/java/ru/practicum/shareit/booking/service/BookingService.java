package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService  {
    BookingDto createBooking(BookingCreateDto bookingDto, int userId);

    BookingDto approveBooking(int bookingId, boolean approved, int userId);

    BookingDto getBooking(int bookingId, int userId);

    List<BookingDto> getAll(BookingState state, int userId);

    List<BookingDto> getAllByOwners(BookingState state, int userId);
}
