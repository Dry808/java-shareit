package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.util.List;

public interface ItemService  {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int itemId, int userId, ItemDto itemDto);

    ItemDtoWithDate getItem(int id);

    List<ItemDtoWithDate> getAllItems(int userId);

    List<ItemDto> searchItem(String item);

    CommentDto createComment(int itemId, int userId, CommentDto commentDto);
}
