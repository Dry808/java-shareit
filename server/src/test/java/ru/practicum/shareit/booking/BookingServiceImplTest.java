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

import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;

import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

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

    @Test
    public void testCreateBooking_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        itemRepository.save(item);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), item.getId(),null,null);
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
        assertThrows(AccessDeniedException.class,() -> bookingService.approveBooking(createdBooking.getId(), true,
                2));
    }
}
