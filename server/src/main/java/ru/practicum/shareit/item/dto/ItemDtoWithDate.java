package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWithDate {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("requestId")
    private Integer request;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    List<CommentDto> comments;
}