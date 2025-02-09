package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository,
                           ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(int userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(Instant.now());
        itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequest(int userId) {
        List<ItemRequest> userRequests = itemRequestRepository.findByRequestorId(userId,
                Sort.by(Sort.Direction.DESC, "created")); // получаем список запросов
        List<ItemDto> item = itemRepository.findByRequestId(userId) // список вещей которые удовлетворяют запросу
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return userRequests.stream()
                .map(ItemRequestMapper::itemRequestDto)
                .map(itemRequestDto -> {
                    itemRequestDto.setItems(item); // устанавливаем список вещей подходящие к запросу
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequest() {
        return itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC)).stream()
                .map(ItemRequestMapper::itemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(int requestId) {
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId).reversed())
                .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(()
                -> new NotFoundException("Запрос не найден")));
        itemRequestDto.setItems(items); // устанавливаем ответы на запрос
        return itemRequestDto;
    }
}
