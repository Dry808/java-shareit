package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated
@Component
public class InMemoryItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int itemsId = 0;

    // Сохранить предмет
    public Item createItem(Item item) {
        item.setId(generateId()); // устанавливаем ID предмета
        return items.put(item.getId(), item);
    }

    // обновить предмет
    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    // получить предмет по ID
    public Item getItem(int id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с ID = " + id + "не найден");
        }
        return items.get(id);
    }

    // получить список всех предметов пользователя
    public List<Item> getAllItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    // поиск предметов
    public  List<Item> searchItem(String text) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                result.add(item);
            }
        }
        return result;
    }

    // метод для генерации ID
    private int generateId() {
        return itemsId++;
    }
}
