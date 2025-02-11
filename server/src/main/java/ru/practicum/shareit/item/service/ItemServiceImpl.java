package ru.practicum.shareit.item.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.CommentMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    // Создать предмет
    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user); // устанавливаем владельца предмета
        if (itemDto.getRequest() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequest()).orElseThrow());
        }

        itemRepository.save(item);

        return ItemMapper.toItemDto(item, commentRepository.findByItemId(item.getId()));
    }

    // Обновить данные предмета
    @Override
    public ItemDto updateItem(int itemId, int userId, ItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Item не найден")); // проверяет наличие Item
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
        return ItemMapper.toItemDto(oldItem, commentRepository.findByItemId(oldItem.getId()));
    }

    // Получить предмет по ID
    @Override
    public ItemDtoWithDate getItem(int id) {
        Item item = itemRepository.findById(id).orElseThrow();

        //  ItemDtoWithDate itemDtoWithDate = itemWithBookingsDate(item);
        ItemDtoWithDate itemDto = ItemMapper.toItemDtoWithDate(item,
                commentRepository.findByItemOwnerId(item.getOwner().getId()));

        // Получаем последнее и следующее бронирование
        Optional<Booking> lastBooking = bookingRepository.findLastBookingByItemId(id);
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItemId(id);
        // Добавляем даты в dto
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(
                booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(
                booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime()));

        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        return itemDto;
    }

    // Список предметов пользователя
    @Override
    public List<ItemDtoWithDate> getAllItems(int userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        return userItems.stream()
                .map(item -> {
                    ItemDtoWithDate itemDto = ItemMapper.toItemDtoWithDate(item,
                            commentRepository.findByItemId(item.getId()));
                    // Получаем последнее и следующее бронирование
                    Optional<Booking> lastBooking = bookingRepository.findLastBookingByItemId(item.getId());
                    Optional<Booking> nextBooking = bookingRepository.findNextBookingByItemId(item.getId());

                    // Добавляем даты в dto
                    lastBooking.ifPresent(booking -> itemDto.setLastBooking(
                            booking.getEnd().atZone(ZoneId.systemDefault()).toLocalDateTime()));
                    nextBooking.ifPresent(booking -> itemDto.setNextBooking(
                            booking.getStart().atZone(ZoneId.systemDefault()).toLocalDateTime()));
                    return itemDto;
                })
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
                .map(item -> ItemMapper.toItemDto(item, commentRepository.findByItemId(item.getId())))// заменяем Item на ItemDTO
                .collect(Collectors.toList());
    }

    // добавление комментариев
    @Override
    public CommentDto createComment(int itemId, int userId, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.toInstant(ZoneOffset.UTC);
        Sort sort = Sort.by(Sort.Direction.DESC, "end");
        List<Booking> pastBookings = bookingRepository.findByBooker_IdAndItem_IdAndStatusIsAndEndBefore(userId,
                itemId, BookingStatus.APPROVED,
                instant,
                sort);
        if (pastBookings.isEmpty()) {
            throw new BookingException("Пользователь с ID=" + userId + " не брал в аренду вещь с ID=" + itemId);
        }

        comment.setCreated(LocalDateTime.now());
        comment.setText(commentDto.getText());
        comment.setItem(itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Инструмент не найден")));
        comment.setAuthor(userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден")));
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}
