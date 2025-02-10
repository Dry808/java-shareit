package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemDto item;
    private ItemRequestDto itemRequest;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(new User(1, "Test User", "test@example.com"));
        item = new ItemDto(1, "Test Item", "Test Description",
                true, null, null);
    }

    @Test
    public void testCreateItemAndGet() {
        itemService.createItem(user.getId(), item);
        Item saveItem = itemRepository.getReferenceById(1);

        assertEquals(1, saveItem.getId());

        ItemDto itemDtoNew = new ItemDto(4, "Name", "New Description", true, null,
                null);
        itemService.createItem(user.getId(), itemDtoNew);
        ItemDtoWithDate createdItem = itemService.getItem(2);

        assertNotNull(createdItem);
        assertEquals(2, createdItem.getId());
        assertEquals("Name", createdItem.getName());
        assertEquals("New Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    public void testUpdate() {
        ItemDto oldItem = itemService.createItem(user.getId(), item);

        ItemDto itemDtoNew = new ItemDto(oldItem.getId(),
                "UpdateName",
                "New Descp",
                true,
                null,
                null);
        ItemDto itemUpdated = itemService.updateItem(oldItem.getId(), user.getId(), itemDtoNew);


        assertEquals(oldItem.getId(), 1);
        assertEquals(itemUpdated.getId(), oldItem.getId());
        assertEquals(itemUpdated.getDescription(), itemDtoNew.getDescription());
        assertEquals(itemUpdated.getName(), itemDtoNew.getName());
    }

    @Test
    public void testSearch() {

        ItemDto itemDtoNew = new ItemDto(1, "AKON", "Test Description", true, null,
                null);
        ItemDto itemDto = new ItemDto(2, "Name", "Test Description", true, null,
                null);

        itemService.createItem(user.getId(), itemDtoNew);
        itemService.createItem(user.getId(), itemDto);

        List<ItemDto> result = itemService.searchItem("Test Description");
        assertEquals(result.size(), 2);
        assertEquals(result.get(1).getId(), 2);

        List<ItemDto> resultName = itemService.searchItem("AKON");

        assertEquals(resultName.size(), 1);
        assertEquals(resultName.get(0).getId(), 1);
    }

    @Test
    public void testCreateCommentByNotBookingUser() {
        itemService.createItem(user.getId(), item);
        CommentDto commentDto = new CommentDto(null, "Test Comment", "Test Author", LocalDateTime.now());

        assertThrows(BookingException.class, () -> itemService.createComment(item.getId(), user.getId(), commentDto));
    }

    @Test
    public void testCreateComment_Success() {
        userRepository.save(user);

        // Создаем предмет и сохраняем его в репозиторий
        ItemDto itemDto = new ItemDto(1, "Item Name", "Item Description", true, null, null);
        ItemDto createdItem = itemService.createItem(user.getId(), itemDto);

        // Создаем пользователя, который будет бронировать предмет
        User userBooker = new User(2, "Booker", "booker@mail.ru");
        userRepository.save(userBooker);

        // Создаем бронирование, которое уже завершено
        Instant start = Instant.now().minusSeconds(2000);
        Instant end = Instant.now().minusSeconds(1000);

        Booking booking2 = new Booking(1, start, end, ItemMapper.toItem(createdItem), userBooker, BookingStatus.APPROVED);
        Booking booking = bookingRepository.save(booking2);

        // Убедимся, что бронирование сохранено корректно
        assertNotNull(booking);
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertEquals(createdItem.getId(), booking.getItem().getId());
        assertEquals(userBooker.getId(), booking.getBooker().getId());

        // Создаем комментарий
        CommentDto commentDto = new CommentDto(null, "Test Comment", null, null);
        CommentDto createdComment = itemService.createComment(createdItem.getId(), userBooker.getId(), commentDto);

        // Проверяем
        assertNotNull(createdComment);
        assertEquals("Test Comment", createdComment.getText());
        assertEquals("Booker", createdComment.getAuthorName());
        assertNotNull(createdComment.getCreated());
    }

    @Test
    public void testUpdateItem_AccessDenied() {
        ItemDto oldItem = itemService.createItem(user.getId(), item);

        User anotherUser = new User(2, "Another User", "another@example.com");
        userRepository.save(anotherUser);

        ItemDto itemDtoNew = new ItemDto(oldItem.getId(),
                "UpdateName",
                "New Descp",
                true,
                null,
                null);

        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(oldItem.getId(), anotherUser.getId(), itemDtoNew));
    }

    @Test
    public void testGetAllItems() {
        // Создаем предметы для пользователя
        ItemDto item1 = new ItemDto(1, "Item 1", "Description 1", true, null, null);
        ItemDto item2 = new ItemDto(2, "Item 2", "Description 2", true, null, null);
        itemService.createItem(user.getId(), item1);
        itemService.createItem(user.getId(), item2);

        // Получаем все предметы пользователя
        List<ItemDtoWithDate> items = itemService.getAllItems(user.getId());

        // Проверяем
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
    }

    @Test
    public void testGetItem_WithBookings() {
        // Создаем предмет
        ItemDto itemDto = new ItemDto(1, "Item Name", "Item Description", true, null, null);
        ItemDto createdItem = itemService.createItem(user.getId(), itemDto);

        // Создаем пользователя, который будет бронировать предмет
        User userBooker = new User(2, "Booker", "booker@mail.ru");
        userRepository.save(userBooker);

        // Создаем бронирование
        Instant start = Instant.now().plusSeconds(1000);
        Instant end = Instant.now().plusSeconds(2000);
        Booking booking = new Booking(1, start, end, ItemMapper.toItem(createdItem), userBooker, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        // Получаем предмет с датами бронирования
        ItemDtoWithDate itemWithBookings = itemService.getItem(createdItem.getId());

        // Проверяем
        assertNotNull(itemWithBookings);
        assertEquals(createdItem.getId(), itemWithBookings.getId());
    }

    @Test
    public void testGetItemNotFound() {
        assertThrows(Exception.class, () -> itemService.getItem(999));
    }

}
