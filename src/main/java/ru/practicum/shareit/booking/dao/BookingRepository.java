package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Методы для PAST бронирований
    List<Booking> findByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime end, Sort sort);
    List<Booking> findByItemOwner_IdAndEndIsBefore(int ownerId, LocalDateTime end, Sort sort);

    // Методы для Current бронирований
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start,
                                                               LocalDateTime end, Sort sort);
    List<Booking> findByItemOwner_IdAndStartIsBeforeAndEndIsAfter(int ownerId, LocalDateTime start,
                                                                         LocalDateTime end,
                                                                         Sort sort);

    // для FUTURE бронирований
    List<Booking> findByBooker_IdAndStartIsAfter(int bookerId, LocalDateTime now, Sort sort);
    List<Booking> findByItemOwner_IdAndStartIsAfter(int ownerId, LocalDateTime now, Sort sort);

    // для бронирований (WAITING, REJECTED, APPROVED, CANCELED)
    List<Booking> findByBooker_IdAndStatus(int bookerId, BookingStatus status, Sort sort);
    List<Booking> findByItemOwner_IdAndStatus(int ownerId, BookingStatus status, Sort sort);

    // Остальные случаи(ALL)
    List<Booking> findByBooker_Id(int bookerId, Sort sort);
    List<Booking> findByItemOwner_Id(int ownerId, Sort sort);

}
