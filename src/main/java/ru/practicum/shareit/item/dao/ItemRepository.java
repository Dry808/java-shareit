package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItem(int id);

    List<Item> getAllItems(int userId);

    List<Item> searchItem(String text);

}
