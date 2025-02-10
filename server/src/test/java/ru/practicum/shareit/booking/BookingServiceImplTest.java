package ru.practicum.shareit.booking;




import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;


    @Test
    public void testCreateBooking_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), null, null);
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Проверяем, что бронирование создано
        assertNotNull(createdBooking);
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
        assertEquals("WAITING", createdBooking.getStatus());
    }


    @Test
    public void testApproveBooking_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200), item.getId(), user.getId(), "WAITING");
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Подтверждаем бронирование
        BookingDto approvedBooking = bookingService.approveBooking(createdBooking.getId(), true, user.getId());

        // Проверяем, что бронирование подтверждено
        assertEquals("APPROVED", approvedBooking.getStatus());
    }

    @Test
    public void testApproveBooking_AccessDenied() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(),
                Instant.now().plusSeconds(3600),
                Instant.now().plusSeconds(7200), item.getId(), null, null);
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Пытаемся подтвердить бронирование пользователем, который не является владельцем предмета
        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(createdBooking.getId(), true,
                2));
    }

    @Test
    public void testGetAllBookingsByOwner_NoItems() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Проверяем, что выбрасывается исключение, если у пользователя нет предметов
        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwners(BookingState.WAITING, user.getId()));
    }

    @Test
    public void testGetAllBookingsByOwner() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), user.getId(), "WAITING");
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Получаем все бронирования для владельца предмета
        List<BookingDto> result = bookingService.getAllByOwners(BookingState.WAITING, user.getId());

        // Проверяем результат
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WAITING", result.get(0).getStatus());
    }

    @Test
    public void testGetAllBookings() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), user.getId(), "WAITING");
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Получаем все бронирования для пользователя
        List<BookingDto> result = bookingService.getAll(BookingState.WAITING, user.getId());

        // Проверяем результат
        assertNotNull(result);
        assertEquals("WAITING", result.get(0).getStatus());
    }

    @Test
    public void testGetBooking_AccessDenied() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), user.getId(), "WAITING");
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Создаем другого пользователя
        User anotherUser = new User(2, "Another User", "another@example.com");
        userRepository.save(anotherUser);

        // Пытаемся получить бронирование от имени другого пользователя
        assertThrows(Exception.class, () -> bookingService.getBooking(createdBooking.getId(), anotherUser.getId()));
    }

    @Test
    public void testApproveBooking_Rejected() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), user.getId(), "WAITING");
        BookingDto createdBooking = bookingService.createBooking(bookingDto, user.getId());

        // Отклоняем бронирование
        BookingDto rejectedBooking = bookingService.approveBooking(createdBooking.getId(), false, user.getId());

        // Проверяем, что бронирование отклонено
        assertEquals("REJECTED", rejectedBooking.getStatus());
    }

    @Test
    public void testCreateBooking_ItemNotFound() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем бронирование с несуществующим предметом
        BookingCreateDto bookingDto = new BookingCreateDto(999, Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), 999, null, null);

        // Проверяем, что бронирование с несуществующим предметом выбрасывает исключение
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
    }

    @Test
    public void testCreateBooking_ItemNotAvailable() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем недоступный предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", false, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(), null, null);

        // Проверяем, что бронирование недоступного предмета выбрасывает исключение
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingDto, user.getId()));
    }
}

