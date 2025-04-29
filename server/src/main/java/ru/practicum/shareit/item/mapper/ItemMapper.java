package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.CommentMapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (Item <-> ItemDto)
 */
public class ItemMapper {
    public static ItemDto toItemDto(Item item, List<Comment> comment) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                comment.stream().map(CommentMapper::toCommentDto).toList()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null
        );
    }


    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoWithDate toItemDtoWithDate(Item item, List<Comment> comments) {
        return new ItemDtoWithDate(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                comments != null ? comments.stream().map(CommentMapper::toCommentDto).toList() : new ArrayList<>()
        );

    }
}
