package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

/**
 * DTO request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private int id;
    private String description;
    private User requestor;
    private Instant created;
    private List<ItemDto> items;
}
