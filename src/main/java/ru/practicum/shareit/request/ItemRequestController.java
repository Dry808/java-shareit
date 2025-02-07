package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")

public class ItemRequestController {
    ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса от пользователя с ID=" + userId);
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получить список запросов пользователя с ID=" + userId);
        return itemRequestService.getRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest() {
        log.info("Получение списка всех запросов");
        return itemRequestService.getAllRequest();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable int requestId) {
        log.info("Получить данные о запросе с ID=" + requestId);
        return itemRequestService.getRequestById(requestId);
    }
}
