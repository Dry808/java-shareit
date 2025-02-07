package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * DTO
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private int id;

    private String name;

    private String description;

    private Boolean available;
    @JsonProperty("requestId")
    private Integer request;
    List<CommentDto> comments;
}
