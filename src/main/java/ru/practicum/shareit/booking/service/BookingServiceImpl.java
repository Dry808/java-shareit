package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@Service
public class BookingServiceImpl implements  BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, int userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(int userId, int bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBooking(int bookingId, int userId) {
        return null;
    }

    @Override
    public List<BookingDto> getAll(BookingState state, int userId) {
        return List.of();
    }

    @Override
    public List<BookingDto> getAllOwners(BookingState state, int userId) {
        return List.of();
    }
}
