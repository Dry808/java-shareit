package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;

    private String name;

    private String description;

    private Boolean available;
    @JsonProperty("requestId")
    private Integer request;
    List<CommentDto> comments;
}
