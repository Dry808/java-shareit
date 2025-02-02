package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    String FIND_LAST_BOOKING = "SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.end < CURRENT_TIMESTAMP " + // Только завершенные бронирования
            "AND b.status = 'APPROVED' " +     // Только подтвержденные бронирования
            "ORDER BY b.end DESC";
    String FIND_NEXT_BOOKING = "SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC";

    // Методы для PAST бронирований
    List<Booking> findByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwner_IdAndEndIsBefore(int ownerId, LocalDateTime end, Sort sort);

    // Методы для Current бронирований
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

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

    // Поиск последней и следующей брони
    @Query(FIND_LAST_BOOKING)
    Optional<Booking> findLastBookingByItemId(@Param("itemId") int itemId);

    @Query(FIND_NEXT_BOOKING)
    Optional<Booking> findNextBookingByItemId(@Param("itemId") int itemId);

    // проверка на бронь
    Boolean existsByBooker_IdAndItem_IdAndStatusIsAndEndBefore(int bookerId,
                                                               int itemId,
                                                               BookingStatus status, Instant time);

    List<Booking> findByBooker_IdAndItem_IdAndStatusIsAndEndBefore(
            int bookerId,
            int itemId,
            BookingStatus status,
            Instant time,
            Sort sort
    );
}
