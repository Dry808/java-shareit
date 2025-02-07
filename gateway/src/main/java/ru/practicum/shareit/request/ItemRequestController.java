package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса от пользователя с ID=" + userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка запросов пользователя с ID=" + userId);
        return itemRequestClient.getRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest() {
        log.info("Получение списка всех запросов");
        return itemRequestClient.getAllRequest();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @PathVariable int requestId) {
        log.info("Получение данных о запросе с ID=" + requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
