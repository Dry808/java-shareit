package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;



import java.util.List;

/**
 * Контроллер Item
 */

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Пользователь с ID=" + userId + " добавил новый предмет");
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") int userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь с ID=" + userId + " добавил комментарий к предмету с ID=" + itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable int itemId,
                                             @RequestHeader("X-Sharer-User-Id") int userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Пользователь с ID=" + userId + " изменил предмет с ID=" + itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение предмета с ID=" + itemId + " для пользователя с ID=" + userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех предметов пользователя с ID=" + userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Поиск предметов по запросу: " + text + " для пользователя с ID=" + userId);
        return itemClient.searchItem(userId, text);
    }
}
