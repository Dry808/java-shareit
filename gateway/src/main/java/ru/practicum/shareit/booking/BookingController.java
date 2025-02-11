package ru.practicum.shareit.booking;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@Autowired
	public BookingController(BookingClient bookingClient) {
		this.bookingClient = bookingClient;
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
												@Valid @RequestBody BookingCreateDto bookingDto) {
		log.info("Пользователь с ID=" + userId + " создал запрос на бронирование");
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable int bookingId,
												 @RequestParam boolean approved,
												 @RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Обработка запроса на бронирование с ID=" + bookingId + " владелец с ID=" + userId);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable int bookingId,
											 @RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Получение данных о бронировании с ID=" + bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBooking(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
												@RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Получение списка всех бронирований пользователя с ID=" + userId);
		return bookingClient.getAllBooking(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
														@RequestHeader("X-Sharer-User-Id") int userId) {
		log.info("Получение списка всех бронирований владельца с ID=" + userId);
		return bookingClient.getAllBookingsByOwner(userId, state);
	}
}
