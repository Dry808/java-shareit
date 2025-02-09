package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testCreateRequest_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем запрос
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", null, null, new ArrayList<>());
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto);

        // Проверяем, что запрос создан
        assertNotNull(createdRequest);
        assertEquals("Description", createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());
        assertEquals(user.getId(), createdRequest.getRequestor().getId());
    }

    @Test
    public void testGetRequest_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем запрос
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", null, null, new ArrayList<>());
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto);

        // Создаем предмет, связанный с запросом
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        item.setRequest(itemRequestRepository.findById(createdRequest.getId()).orElseThrow());
        itemRepository.save(item);

        // Получаем запросы пользователя
        List<ItemRequestDto> requests = itemRequestService.getRequest(user.getId());

        // Проверяем, что запросы получены
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals("Description", requests.get(0).getDescription());
        assertNotNull(requests.get(0).getItems());
        assertEquals(1, requests.get(0).getItems().size());
    }

    @Test
    public void testGetRequest_UserNotFound() {
        // Пытаемся получить запросы для несуществующего пользователя
        List<ItemRequestDto> requests = itemRequestService.getRequest(9299);

        // Проверяем, что список запросов пуст
        assertTrue(requests.isEmpty());
    }


    @Test
    public void testGetRequestById_Success() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем запрос
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", null, null, new ArrayList<>());
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto);

        // Создаем предмет, связанный с запросом
        Item item = new Item(1, "Item Name", "Item Description", true, user, null);
        item.setRequest(itemRequestRepository.findById(createdRequest.getId()).orElseThrow());
        itemRepository.save(item);

        // Получаем запрос по ID
        ItemRequestDto requestById = itemRequestService.getRequestById(createdRequest.getId());

        // Проверяем, что запрос получен
        assertNotNull(requestById);
        assertEquals("Description", requestById.getDescription());
        assertNotNull(requestById.getItems());
        assertEquals(1, requestById.getItems().size());
    }

    @Test
    public void testGetRequestById_RequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999));
    }
}

