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
    public void testCreateRequest_UserNotFound() {
        // Пытаемся создать запрос для несуществующего пользователя
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", null, null, new ArrayList<>());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(999, itemRequestDto));
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
    public void testGetAllRequest_Success() {
        // Создаем пользователей и сохраняем их в репозиторий
        User user1 = new User(1, "User1", "user1@example.com");
        User user2 = new User(2, "User2", "user2@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Создаем запросы
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1, "Description 1", null, null, new ArrayList<>());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2, "Description 2", null, null, new ArrayList<>());
        itemRequestService.createRequest(user1.getId(), itemRequestDto1);
        itemRequestService.createRequest(user2.getId(), itemRequestDto2);

        // Получаем все запросы
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequest();

        // Проверяем, что запросы получены
        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
        assertEquals("Description 2", allRequests.get(0).getDescription());
        assertEquals("Description 1", allRequests.get(1).getDescription());
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


    public void testGetRequestById_WithItems() {
        // Создаем пользователя и сохраняем его в репозиторий
        User user = new User(1, "User", "user@example.com");
        userRepository.save(user);

        // Создаем запрос
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description", null, null, new ArrayList<>());
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto);

        // Создаем предметы, связанные с запросом
        Item item1 = new Item(1, "Item 1", "Description 1", true, user, null);
        Item item2 = new Item(2, "Item 2", "Description 2", true, user, null);
        item1.setRequest(itemRequestRepository.findById(createdRequest.getId()).orElseThrow());
        item2.setRequest(itemRequestRepository.findById(createdRequest.getId()).orElseThrow());
        itemRepository.save(item1);
        itemRepository.save(item2);

        // Получаем запрос по ID
        ItemRequestDto requestById = itemRequestService.getRequestById(createdRequest.getId());

        // Проверяем, что запрос получен с предметами
        assertNotNull(requestById);
        assertEquals("Description", requestById.getDescription());
        assertNotNull(requestById.getItems());
        assertEquals(2, requestById.getItems().size());
    }
}

