package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.util.List;

/**
 * DTO request
 */
@Data
@AllArgsConstructor
public class ItemRequestDto {
    private int id;
    @NotNull
    private String description;
    private UserDto requestor;
    private Instant created;
    private List<ItemDto> items;
}
