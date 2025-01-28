package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // Создать предмет
    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user); // устанавливаем владельца предмета
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    // Обновить данные предмета
    @Override
    public ItemDto updateItem(int itemId, int userId, ItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(); // проверяет наличие Item
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
        itemRepository.save(oldItem);
        return ItemMapper.toItemDto(oldItem);
    }

    // Получить предмет по ID
    @Override
    public ItemDto getItem(int id) {
        return ItemMapper.toItemDto(itemRepository.findById(id).orElseThrow());
    }

    // Получить список всех предметов пользователя по его ID
    @Override
    public List<ItemDto> getAllItems(int userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        return userItems.stream()
                .map(ItemMapper::toItemDto) // заменяем Item на ItemDTO
                .collect(Collectors.toList());
    }

    // Поиск предмета
    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.findItemByNameAndDescription(text);
        return itemList.stream()
                .filter(Item::getAvailable) // фильтруем поиск только по тем предметам которые доступны
                .map(ItemMapper::toItemDto) // заменяем Item на ItemDTO
                .collect(Collectors.toList());
    }

}
