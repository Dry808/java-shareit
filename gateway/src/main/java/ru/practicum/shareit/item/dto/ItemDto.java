package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO item
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @JsonProperty("requestId")
    private Integer request;
    private List<CommentDto> comments;
}
