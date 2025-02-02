package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int itemId, int userId, ItemDto itemDto);

    ItemDto getItem(int id);

    List<ItemDto> getAllItems(int userId);

    List<ItemDto> searchItem(String item);

}
