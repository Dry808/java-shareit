package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface BookingService  {
    BookingDto createBooking(BookingDto bookingDto, int userId);

    BookingDto approveBooking(int userId, int bookingId, boolean approved);

    BookingDto getBooking(int bookingId, int userId);

    List<BookingDto> getAll(BookingState state, int userId);

    List<BookingDto> getAllOwners(BookingState state, int userId);
}
