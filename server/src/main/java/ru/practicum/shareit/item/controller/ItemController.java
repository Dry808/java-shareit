package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * Контроллер Item
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        log.info("Пользователь c ID=" + userId + " добавил новый предмет c ID=" + itemDto.getId());
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable int itemId,
                                    @RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Пользователь c ID=" + userId + " изменил предмет c ID=" + itemDto.getId());
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate getItem(@PathVariable int itemId) {
        log.info("Получение предмета с ID=" + itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDtoWithDate> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение списка всех предметов пользователя с ID=" + userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Поиск предметов по запросу: " + text);
        return itemService.searchItem(text);
    }
}
