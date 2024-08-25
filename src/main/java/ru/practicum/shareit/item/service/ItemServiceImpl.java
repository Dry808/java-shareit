package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemStorage;
    private UserRepository userStorage;

    ItemServiceImpl(ItemRepository itemStorage, UserRepository userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        User user = userStorage.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user); // устанавливаем владельца вещи
        itemStorage.createItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int itemId, int userId, ItemDto itemDto) {
        Item oldItem = itemStorage.getItem(itemId); // проверяет наличие Item
        if (oldItem.getOwner().getId() != userId) { // проверка доступа
            throw new AccessDeniedException("Вы не можете редактировать Item пользователя c ID=" + oldItem.getId());
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        itemStorage.updateItem(oldItem);
        return ItemMapper.toItemDto(oldItem);
    }

    @Override
    public ItemDto getItem(int id) {
        return ItemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItems(int userId) {
        List<Item> userItems = itemStorage.getAllItems(userId);
        return userItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemStorage.searchItem(text);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
