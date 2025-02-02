package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;

    }

    @Override
    public BookingDto createBooking(BookingCreateDto bookingDto, int userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден")));
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow(()
                -> new NotFoundException("Инструмент не найден")));
        ;

        if (!booking.getItem().getAvailable()) {        // Проверка на доступность товара
            throw new RuntimeException("Инструмент не доступен для бронирования");
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(int bookingId, boolean approved, int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (userId != booking.getItem().getOwner().getId()) {
            throw new AccessDeniedException("Вы не можете подверждать бронирование чужой вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new AccessDeniedException("Доступ запрещен");
        }
    }

    @Override
    public List<BookingDto> getAll(BookingState state, int userId) {
        List<Booking> bookings;
        switch (state) {
            case PAST -> bookings = bookingRepository.findByBooker_IdAndEndIsBefore(
                    userId,
                    LocalDateTime.now(),
                    Sort.sort(Booking.class));

            case CURRENT -> bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Sort.sort(Booking.class));

            case FUTURE -> bookings = bookingRepository.findByBooker_IdAndStartIsAfter(
                    userId,
                    LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "start"));

            case WAITING -> bookings = bookingRepository.findByBooker_IdAndStatus(
                    userId,
                    BookingStatus.WAITING,
                    Sort.by(Sort.Direction.DESC, "start"));

            case REJECTED -> bookings = bookingRepository.findByBooker_IdAndStatus(
                    userId,
                    BookingStatus.REJECTED,
                    Sort.by(Sort.Direction.DESC, "start"));

            default -> bookings = bookingRepository.findByBooker_Id(
                    userId,
                    Sort.by(Sort.Direction.DESC, "start"));
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwners(BookingState state, int userId) {
        if (!itemRepository.existsByOwner_Id(userId)) {
            throw new NotFoundException("У пользователя с ID" + userId + "нет вещей");
        }
        List<Booking> bookings;
        switch (state) {
            case PAST -> bookings = bookingRepository.findByItemOwner_IdAndEndIsBefore(
                    userId,
                    LocalDateTime.now(),
                    Sort.sort(Booking.class));

            case CURRENT -> bookings = bookingRepository.findByItemOwner_IdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Sort.sort(Booking.class));

            case FUTURE -> bookings = bookingRepository.findByItemOwner_IdAndStartIsAfter(
                    userId,
                    LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "start"));

            case WAITING -> bookings = bookingRepository.findByItemOwner_IdAndStatus(
                    userId,
                    BookingStatus.WAITING,
                    Sort.by(Sort.Direction.DESC, "start"));

            case REJECTED -> bookings = bookingRepository.findByItemOwner_IdAndStatus(
                    userId,
                    BookingStatus.REJECTED,
                    Sort.by(Sort.Direction.DESC, "start"));

            default -> bookings = bookingRepository.findByItemOwner_Id(
                    userId,
                    Sort.by(Sort.Direction.DESC, "start"));
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }
}
