package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }


}
